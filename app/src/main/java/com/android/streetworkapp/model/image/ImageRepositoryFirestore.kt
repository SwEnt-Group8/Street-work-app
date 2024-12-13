package com.android.streetworkapp.model.image

import android.util.Log
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.storage.S3StorageClient
import com.android.streetworkapp.model.user.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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

      val parkImage = ParkImage(imageUrl = imageUrl, userId = userId, username = user.username)

      val collectionId = park.imagesCollectionId
      if (collectionId.isEmpty()) { // no imagesCollection for the park yet
        val collectionRef = db.collection(COLLECTION_PATH).document()
        val newCollectionId = collectionRef.id

        val imagesCollection = ParkImageCollection(id = newCollectionId, listOf(parkImage))
        // could have some loose collections if the call fails here, can't be bothered to handle
        // this case
        db.collection(COLLECTION_PATH).document(collectionRef.id).set(imagesCollection).await()
        this.parkRepository.addImagesCollection(park.pid, newCollectionId)
      } else { // there already exists an imagesCollection
        val imagesCollection = db.collection(COLLECTION_PATH).document(collectionId)
        imagesCollection.update("images", FieldValue.arrayUnion(parkImage)).await()
      }
    } catch (e: Exception) {
      Log.d(
          DEBUG_PREFIX,
          e.message
              ?: "An exception occurred but the message associated with couldn't be retrieved.")
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
      val document = db.collection(COLLECTION_PATH).document(park.imagesCollectionId).get().await()
      require(document.exists()) {
        "The ImagesCollection linked by this park doesn't exist, the entry in the db is corrupted."
      }

      val parkImages = document["images"] as? List<*>
      return this.documentToParkImageDatabase(parkImages)
    } catch (e: Exception) {
      Log.d(
          DEBUG_PREFIX,
          e.message
              ?: "An exception occurred but the message associated with it couldn't be retrieved.")
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
            it) // there's an edge case where the image is deleted from firestore but not the db,
                // won't handle this case. Would probably need a proper backend with some logging
                // for this
      }

      return true
    } catch (e: Exception) {
      Log.d(
          DEBUG_PREFIX,
          e.message
              ?: "An exception occurred but the message associated with it couldn't be retrieved.")
      return false
    }
  }

    /**
     * Updates the score of the image with hash imageHash in document imageCollectionId
     * @param imageCollectionId The collection the image belongs to.
     * @param imageUrl The url of the image of whom to register the vote to.
     * @param vote The vote type. True if a positive vote, false if a negative vote.
     */
  override suspend fun imageVote(
      imageCollectionId: String,
      imageUrl: String,
      vote: VOTE_TYPE
  ): Boolean {
    require(imageCollectionId.isNotEmpty()) { "Empty imageCollectionId." }
    require(imageUrl.isNotEmpty()) { "Empty imageHash." }

    try {
      val docRef =
          db.collection(ImageRepositoryFirestore.COLLECTION_PATH).document(imageCollectionId)
      val document = docRef.get().await()
      val images = document.get("images") as? List<Map<String, Any>> ?: return false

        //pretty inefficient to go through the whole list but whatever
      val updatedImages =
          images.map { image ->
            if (image["imageUrl"] == imageUrl) {
              val currentVotes = image["ratings"] as? List<Int> ?: listOf(0, 0)
              val updatedVotes =
                  when(vote) {
                      VOTE_TYPE.POSITIVE -> listOf(currentVotes[0] + vote.value, currentVotes[1])
                      VOTE_TYPE.NEGATIVE -> listOf(currentVotes[0], currentVotes[1] + vote.value)
                  }

              image.toMutableMap().apply { put("votes", updatedVotes) }
            } else {
              image // No change for this image
            }
          }

      docRef.update("images", updatedImages).await()
      return true
    } catch (e: Exception) {
      Log.d(
          DEBUG_PREFIX,
          e.message
              ?: "An exception occurred but the message associated with it couldn't be retrieved.")
      return false
    }
  }

  /**
   * Deletes all the images related to a user
   *
   * @param userId The user id to whom we delete all the related pictures
   */
  override suspend fun deleteAllImagesFromUser(userId: String) {
    TODO("Not yet implemented")
  }



  /**
   * Register a listener to a specific imageCollectionId
   *
   * @param imageCollectionId The id of the document to listen to.
   * @param onDocumentChange The callback to be called each time the document changes
   */
  override fun registerCollectionListener(imageCollectionId: String, onDocumentChange: () -> Unit) {
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
              ?: "An exception occurred but the message associated with it couldn't be retrieved.")
    }
  }

  /**
   * Convert a document entry into an ParkImageDatabase, skips the image if the decoding for it
   * fails
   */
  private fun documentToParkImageDatabase(parkImages: List<*>?): List<ParkImage> {
    // Map each item to a ParkImage object, if an entry is invalid just skip it
    return parkImages?.mapNotNull { image ->
      runCatching {
            val imageMap = image as? Map<*, *> ?: return@mapNotNull null

            val imageUrl = imageMap["imageUrl"] as? String ?: return@mapNotNull null
            val userId = imageMap["userId"] as? String ?: return@mapNotNull null
            val username = imageMap["username"] as? String ?: return@mapNotNull null
            val ratingMap = imageMap["rating"] as? Map<*, *> ?: return@mapNotNull null
            val first = (ratingMap["first"] as? Number)?.toInt() ?: return@mapNotNull null
            val second = (ratingMap["second"] as? Number)?.toInt() ?: return@mapNotNull null
            val uploadDate = imageMap["uploadDate"] as? Timestamp ?: return@mapNotNull null

            // Create the ParkImage object if all fields are valid
            ParkImage(
                imageUrl = imageUrl,
                userId = userId,
                username = username,
                rating = Pair(first, second),
                uploadDate = uploadDate)
          }
          .getOrNull()
    } ?: return emptyList()
  }
}


