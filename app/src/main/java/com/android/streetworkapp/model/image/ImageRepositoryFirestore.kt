package com.android.streetworkapp.model.image

import android.util.Log
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.user.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ImageRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val parkRepository: ParkRepository,
    private val userRepository: UserRepository
) : ImageRepository {
  companion object {
    const val DEBUG_PREFIX = "ImageRepositoryFirestore:"
    const val COLLECTION_PATH = "parkImages"
  }

  /**
   * Uploads an image in b64 format into our database
   *
   * @param imageB64 The base64 encoded image
   * @param parkId The parkId the image will be linked to
   * @param userId The id of the image uploader
   */
  override suspend fun uploadImage(imageB64: String, parkId: String, userId: String) {
    require(imageB64.isNotEmpty()) { "imageB64 should not be empty." }
    require(parkId.isNotEmpty()) { "parkId cannot be empty." }

    require(userId.isNotEmpty()) { "userId cannot be empty." }

    val user = this.userRepository.getUserByUid(userId)
    require(user != null) { "Invalid userId." }

    try {
      val park = this.parkRepository.getParkByPid(parkId)
      require(park != null) { "Invalid parkId." }

      val parkImage =
          ParkImageDatabase(imageB64 = imageB64, userId = userId, username = user.username)

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
        val newImage = ParkImageDatabase(imageB64, userId, user.username)
        imagesCollection.update("images", FieldValue.arrayUnion(newImage)).await()
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
  override suspend fun retrieveImages(park: Park): List<ParkImageDatabase> {
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
      // Map each item to a ParkImage object, if an entry is invalid just skip it
      // I'm not going to do a separate function for deserialization, It's only used here
      return parkImages?.mapNotNull { image ->
        runCatching {
              val imageMap = image as? Map<*, *> ?: return@mapNotNull null

              val imageB64 = imageMap["imageB64"] as? String ?: return@mapNotNull null
              val userId = imageMap["userId"] as? String ?: return@mapNotNull null
              val username = imageMap["username"] as? String ?: return@mapNotNull null
              val ratingMap = imageMap["rating"] as? Map<*, *> ?: return@mapNotNull null
              val first = (ratingMap["first"] as? Number)?.toInt() ?: return@mapNotNull null
              val second = (ratingMap["second"] as? Number)?.toInt() ?: return@mapNotNull null
              val uploadDate = imageMap["uploadDate"] as? Timestamp ?: return@mapNotNull null

              // Create the ParkImage object if all fields are valid
              ParkImageDatabase(
                  imageB64 = imageB64,
                  userId = userId,
                  username = username,
                  rating = Pair(first, second),
                  uploadDate = uploadDate)
            }
            .getOrNull()
      } ?: return emptyList()
    } catch (e: Exception) {
      Log.d(
          DEBUG_PREFIX,
          e.message
              ?: "An exception occurred but the message associated with it couldn't be retrieved.")
      return emptyList() // return emptyList in case of error, this way it doesn't propagate
    }
  }
}
