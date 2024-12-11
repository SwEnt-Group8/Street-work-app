package com.android.streetworkapp.model.image

import com.android.streetworkapp.model.park.Park

interface ImageRepository {
  /**
   * Uploads the image to the db.
   *
   * @param base64ImageHash The hash of the base64 representation of the image.
   * @param imageB64 the base64 representation of the image.
   * @param parkId the id of the park the image will be linked to.
   * @param userId the id of the user that uploaded the image.
   */
  suspend fun uploadImage(base64ImageHash: String, imageB64: String, parkId: String, userId: String)

  /** Retrieves all the images linked from the park. */
  suspend fun retrieveImages(park: Park): List<ParkImageDatabase>


  /**
   * Deletes the image corresponding the to hash in the document with imageCollectionId
   */
  suspend fun deleteImage(imageCollectionId: String, imageHash: String): Boolean

  /**
   * Updates the score of the image with hash imageHash in document imageCollectionId
   * @param voteType The vote type. True if a positive vote, false if a negative vote.
   */
  suspend fun imageVote(imageCollectionId: String, imageHash: String, voteType: Boolean): Boolean

  /**
   * Deletes all the images related to a user
   * @param userId The user id to whom we delete all the related pictures
   */
  suspend fun deleteAllImagesFromUser(userId: String)

  /**
   * Register a listener to a specific imageCollectionId
   * @param imageCollectionId The id of the document to listen to.
   * @param onDocumentChange The callback to be called each time the document changes
   */
  fun registerCollectionListener(
    imageCollectionId: String,
    onDocumentChange: () -> Unit
  )
}
