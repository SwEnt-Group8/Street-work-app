package com.android.streetworkapp.model.image

import android.util.Log
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ImageRepositoryFirestore(private val db: FirebaseFirestore, private val parkRepository: ParkRepository, private val userRepository: UserRepository): ImageRepository {
    companion object {
        const val DEBUG_PREFIX = "ImageRepositoryFirestore:"
        const val COLLECTION_PATH = "parkImages"
    }

    /**
     * Uploads an image in b64 format into our database
     * @param imageB64 The base64 encoded image
     * @param parkId The parkId the image will be linked to
     * @param userId The id of the image uploader
     */
    override suspend fun uploadImage(imageB64: String, parkId: String, userId: String) {
        require(imageB64.isNotEmpty()) { "imageB64 should not be empty." }
        require(parkId.isNotEmpty()) {"parkId cannot be empty."}
        require(userId.isNotEmpty() && this.userRepository.getUserByUid(userId) != null) {"Invalid userId."}

        try {
            //checking if the park already has an image collection
            val park = this.parkRepository.getParkByPid(parkId) ?: throw IllegalArgumentException("Invalid parkId")
            val parkImage = ParkImage(
                imageB64 = imageB64,
                userId = userId
            )

            val collectionId = park.imagesCollectionId
            if (collectionId.isEmpty()) { //no imagesCollection for the park yet
                val collectionRef = db.collection(COLLECTION_PATH).document()
                val newCollectionId = collectionRef.id

                val imagesCollection = ParkImageCollection(id = newCollectionId, listOf(parkImage))
                //could have some loose collections if the call fails here, not going to handle this case
                db.collection(COLLECTION_PATH).document(collectionRef.id).set(imagesCollection).await()
                this.parkRepository.addImagesCollection(park.pid, newCollectionId)
            } else { //there already exists an imagesCollection
                val imagesCollection = db.collection(COLLECTION_PATH).document(collectionId)
                val newImage = ParkImage(imageB64, userId)
                imagesCollection.update("images", FieldValue.arrayUnion(newImage)).await()
            }
        } catch (e: Exception) {
            Log.d(DEBUG_PREFIX, e.message ?: "An exception occurred but the message associated with couldn't be retrieved.")
        }

    }

    override suspend fun retrieveImages(park: Park): List<String> {
        return emptyList()
    }

    /*
    internal fun documentToUser(document: DocumentSnapshot): User? {
        return try {
            val uid = document.id
            val username = document.getString("username") ?: return null
            // Safely handle the 'friends' field
            val friends =
                try {
                    document["friends"] as? List<*> ?: emptyList<String>()
                } catch (e: Exception) {
                    Log.e("FirestoreError", "Error retrieving friends list", e)
                    emptyList<String>() // Return an empty list in case of an exception
                }

            val validFriends = friends.filterIsInstance<String>()
            User(
                uid = uid,
                username = username,
                email = email,
                score = score,
                friends = validFriends,
                picture = picture)
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error converting document to User", e)
            null
        }
    }*/
}