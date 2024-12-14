package com.android.streetworkapp.model.image

import android.util.Log
import androidx.core.graphics.convertTo
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.storage.S3StorageClient
import com.android.streetworkapp.model.user.UserRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class ImageRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val storageClient: S3StorageClient,
    private val parkRepository: ParkRepository,
    private val userRepository: UserRepository
) : ImageRepository {
    companion object {
        const val DEBUG_PREFIX = "ImageRepositoryFirestore:"
        const val COLLECTION_PATH = "parkImages"
    }

    /**
     * Uploads an image into our s3 provider and stores the url with relevant infos in our firebase
     * db.
     *
     * @param uniqueImageIdentifier The hash of the base64 representation of our image.
     * @param imageData The data of the image.
     * @param parkId The parkId the image will be linked to.
     * @param userId The id of the image uploader.
     */
    override suspend fun uploadImage(
        uniqueImageIdentifier: String,
        imageData: ByteArray,
        parkId: String,
        userId: String
    ) {
        require(uniqueImageIdentifier.isNotEmpty()) { "uniqueImageIdentifier should not be empty." }
        require(parkId.isNotEmpty()) { "parkId cannot be empty." }

        require(userId.isNotEmpty()) { "userId cannot be empty." }

        val user = this.userRepository.getUserByUid(userId)
        require(user != null) { "Invalid userId." }

        try {
            val park = this.parkRepository.getParkByPid(parkId)
            require(park != null) { "Invalid parkId." }

            // TODO: get image file type, for now hardcoded
            val imageUrl =
                this.storageClient.uploadFile("${parkId}/${uniqueImageIdentifier}.jpg", imageData)
            require(imageUrl != null) { "Failed to upload image to s3 provider." }

            val parkImage =
                ParkImage(imageUrl = imageUrl, userId = userId, username = user.username)

            val collectionId = park.imagesCollectionId
            if (collectionId.isEmpty()) { // no imagesCollection for the park yet
                val collectionRef = db.collection(COLLECTION_PATH).document()
                val newCollectionId = collectionRef.id

                val imagesCollection = ParkImageCollection(id = newCollectionId, listOf(parkImage))
                // could have some loose collections if the call fails here, can't be bothered to handle
                // this case
                db.collection(COLLECTION_PATH).document(collectionRef.id).set(imagesCollection)
                    .await()
                this.parkRepository.addImagesCollection(park.pid, newCollectionId)
            } else { // there already exists an imagesCollection
                val imagesCollection = db.collection(COLLECTION_PATH).document(collectionId)
                imagesCollection.update("images", FieldValue.arrayUnion(parkImage)).await()
            }
        } catch (e: Exception) {
            Log.d(
                DEBUG_PREFIX,
                e.message
                    ?: "An exception occurred but the message associated with couldn't be retrieved."
            )
        }
    }

    /**
     * Retrieves all the images linked to the park.
     *
     * @param park The park linked to the images
     */
    override suspend fun retrieveImages(park: Park): List<ParkImage> {
        require(this.parkRepository.getParkByPid(park.pid) != null) { "Invalid ParkId." }

        if (park.imagesCollectionId.isEmpty())
            return emptyList() // no collection setup yet, just return no images

        try {
            // checking the collection linked to the park is valid
            val document =
                db.collection(COLLECTION_PATH).document(park.imagesCollectionId).get().await()
            require(document.exists()) {
                "The ImagesCollection linked by this park doesn't exist, the entry in the db is corrupted."
            }

            val imageCollection = document.toObject(ParkImageCollection::class.java)
            return imageCollection?.images ?: emptyList()
        } catch (e: Exception) {
            Log.d(
                DEBUG_PREFIX,
                e.message
                    ?: "An exception occurred but the message associated with it couldn't be retrieved."
            )
            return emptyList() // return emptyList in case of error, this way it doesn't propagate
        }
    }

    /** Deletes the image corresponding the to hash in the document with imageCollectionId */
    override suspend fun deleteImage(imageCollectionId: String, imageUrl: String): Boolean {
        require(imageCollectionId.isNotEmpty()) { "Empty imageCollectionId." }

        try {
            val docRef =
                db.collection(ImageRepositoryFirestore.COLLECTION_PATH).document(imageCollectionId)
            val document = docRef.get().await()
            val images = document.get("images") as? List<Map<String, Any>> ?: return false
            val imageToRemove = images.firstOrNull { it["imageUrl"] == imageUrl } ?: return false

            docRef.update("images", FieldValue.arrayRemove(imageToRemove)).await()

            val fileKey = this.storageClient.extractKeyFromUrl(imageUrl)

            fileKey?.let {
                this.storageClient.deleteObjectFromKey(
                    it
                ) // there's an edge case where the image is deleted from firestore but not the db,
                // won't handle this case. Would probably need a proper backend with some logging
                // for this
            }

            return true
        } catch (e: Exception) {
            Log.d(
                DEBUG_PREFIX,
                e.message
                    ?: "An exception occurred but the message associated with it couldn't be retrieved."
            )
            return false
        }
    }

    /**
     * Updates the score of the image with hash imageHash in document imageCollectionId
     *
     * @param imageCollectionId The collection the image belongs to.
     * @param imageUrl The url of the image of whom to register the vote to.
     * @param voterUID The uid of the voter.
     * @param vote The vote type. True if a positive vote, false if a negative vote.
     */
    override suspend fun imageVote(
        imageCollectionId: String,
        imageUrl: String,
        voterUID: String,
        vote: VOTE_TYPE
    ): Boolean {
        require(imageCollectionId.isNotEmpty()) { "Empty imageCollectionId." }
        require(imageUrl.isNotEmpty()) { "Empty imageUrl." }

        try {
            val docRef =
                db.collection(ImageRepositoryFirestore.COLLECTION_PATH).document(imageCollectionId)
            val document = docRef.get().await()
            val imageCollection = document.toObject(ParkImageCollection::class.java)

            // pretty inefficient to go through the whole list but the data will be pretty small anyways
            imageCollection?.images?.let {
                val updatedImages =
                    it.map { image ->
                        if (image.imageUrl == imageUrl) { //identifying the images by their url
                            if (image.rating.positiveVotesUids.contains(voterUID) || image.rating.negativeVotesUids.contains(voterUID))
                                return@map image

                            val updatedRating =
                                when (vote) {
                                    VOTE_TYPE.POSITIVE -> image.rating.copy(positiveVotes = image.rating.positiveVotes + vote.value, positiveVotesUids = image.rating.positiveVotesUids + voterUID)
                                    VOTE_TYPE.NEGATIVE -> image.rating.copy(negativeVotes = image.rating.negativeVotes + vote.value, negativeVotesUids = image.rating.negativeVotesUids + voterUID)
                                }

                            return@map image.copy(rating = updatedRating)
                        } else {
                            return@map image // No change for this image
                        }
                    }

                docRef.update("images", updatedImages).await()
                return true
            } ?: return false
        } catch (e: Exception) {
            Log.d(
                DEBUG_PREFIX,
                e.message
                    ?: "An exception occurred but the message associated with it couldn't be retrieved."
            )
            return false
        }
    }

    /**
     * Removes the user's vote from the image
     * @param imageCollectionId The collection id the image is part of.
     * @param imageUrl The url of the image.
     * @param userId The userId of the vote to remove.
     */
    override suspend fun retractImageVote(imageCollectionId: String, imageUrl: String, userId: String): Boolean {
        try {
            val docRef =
                db.collection(ImageRepositoryFirestore.COLLECTION_PATH).document(imageCollectionId)
            val document = docRef.get().await()
            val imageCollection = document.toObject(ParkImageCollection::class.java)

            // pretty inefficient to go through the whole list but the data will be pretty small anyways
            imageCollection?.images?.let {
                val updatedImages =
                    it.map { image ->
                        if (image.imageUrl == imageUrl) { //identifying the images by their url
                            val updatedRating: ImageRating?
                            if (image.rating.positiveVotesUids.contains(userId)) {
                                updatedRating = image.rating.copy(positiveVotes = image.rating.positiveVotes - 1, positiveVotesUids = image.rating.positiveVotesUids - userId)
                            } else if (image.rating.negativeVotesUids.contains(userId)) {
                                updatedRating = image.rating.copy(negativeVotes = image.rating.negativeVotes - 1, negativeVotesUids = image.rating.positiveVotesUids - userId)
                            } else {
                                return false //userId was not in the list, return false
                            }

                            return@map image.copy(rating = updatedRating)
                        } else {
                            return@map image // No change for this image
                        }
                    }

                docRef.update("images", updatedImages).await()
                return true
            } ?: return false
        } catch (e: Exception) {
            Log.d(
                DEBUG_PREFIX,
                e.message
                    ?: "An exception occurred but the message associated with it couldn't be retrieved."
            )
            return false
        }
    }

    /**
     * Deletes all the images related to a user
     *
     * @param userId The user id to whom we delete all the data from. (pictures uploaded and ratings)
     */
    //TODO: to be tested
    override suspend fun deleteAllDataFromUser(userId: String) {
        require(userId.isNotEmpty()) {"Empty userId."}

        try {
            val documents = db.collection(ImageRepositoryFirestore.COLLECTION_PATH).get()
                .await() //get all documents from collection
            for (document in documents) {
                val collection = document.toObject(ParkImageCollection::class.java)
                for (image in collection.images) {
                    if (image.userId == userId) {
                        document.reference.update("images", collection.images - image)
                    } else if (image.rating.positiveVotesUids.contains(userId) || image.rating.negativeVotesUids.contains(userId))
                        this.retractImageVote(collection.id, image.imageUrl, userId) //there's a bit of duplicate code reusing this function here but the overhead will be small anyways
                }
            }
        } catch (e: Exception) {
            Log.d(
                DEBUG_PREFIX,
                e.message
                    ?: "An exception occurred but the message associated with it couldn't be retrieved."
            )
        }
    }

    /**
     * Register a listener to a specific imageCollectionId
     *
     * @param imageCollectionId The id of the document to listen to.
     * @param onDocumentChange The callback to be called each time the document changes
     */
    override fun registerCollectionListener(
        imageCollectionId: String,
        onDocumentChange: () -> Unit
    ) {
        require(imageCollectionId.isNotEmpty()) { "Empty imageCollectionId." }
        try {
            val docRef =
                db.collection(ImageRepositoryFirestore.COLLECTION_PATH).document(imageCollectionId)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d(ImageRepositoryFirestore.DEBUG_PREFIX, "Error listening for changes: $e")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    onDocumentChange()
                }
            }
        } catch (e: Exception) {
            Log.d(
                DEBUG_PREFIX,
                e.message
                    ?: "An exception occurred but the message associated with it couldn't be retrieved."
            )
        }
    }
}

