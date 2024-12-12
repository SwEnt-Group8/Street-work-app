package com.android.streetworkapp.model.image

import com.android.streetworkapp.model.park.Park

interface ImageRepository {
  /**
   * Uploads an image into our s3 provider and stores the url with relevant infos in our firebase
   * db.
   *
   * @param uniqueImageIdentifier The hash of the base64 representation of our image.
   * @param imageData The data of the image.
   * @param parkId The parkId the image will be linked to.
   * @param userId The id of the image uploader.
   */
  suspend fun uploadImage(
      uniqueImageIdentifier: String,
      imageData: ByteArray,
      parkId: String,
      userId: String
  )

  /** Retrieves all the images linked from the park. */
  suspend fun retrieveImages(park: Park): List<ParkImage>

  /** Deletes the image corresponding the to hash in the document with imageCollectionId */
  suspend fun deleteImage(imageCollectionId: String, imageUrl: String): Boolean
  /**
   * Updates the score of the image with hash imageHash in document imageCollectionId
   *
   * @param voteType The vote type. True if a positive vote, false if a negative vote.
   */
  suspend fun imageVote(imageCollectionId: String, imageHash: String, voteType: Boolean): Boolean

  /**
   * Deletes all the images related to a user
   *
   * @param userId The user id to whom we delete all the related pictures
   */
  suspend fun deleteAllImagesFromUser(userId: String)

  /**
   * Register a listener to a specific imageCollectionId
   *
   * @param imageCollectionId The id of the document to listen to.
   * @param onDocumentChange The callback to be called each time the document changes
   */
  fun registerCollectionListener(imageCollectionId: String, onDocumentChange: () -> Unit)
}
