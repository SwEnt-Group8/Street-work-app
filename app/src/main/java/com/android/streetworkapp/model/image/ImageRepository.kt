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

  /** Deletes the image corresponding to the imageUrl */
  suspend fun deleteImage(imageCollectionId: String, imageUrl: String): Boolean

  /**
   * Updates the score of the image with hash imageHash in document imageCollectionId
   *
   * @param imageCollectionId The collection the image belongs to.
   * @param imageUrl The url of the image of whom to register the vote to.
   * @param voterUID The uid of the voter.
   * @param vote The vote type. True if a positive vote, false if a negative vote.
   */
  suspend fun imageVote(
      imageCollectionId: String,
      imageUrl: String,
      voterUID: String,
      vote: VOTE_TYPE
  ): Boolean

  /**
   * Removes the user's vote from the image
   *
   * @param imageCollectionId The collection id the image is part of.
   * @param imageUrl The url of the image.
   * @param userId The userId of the vote to remove.
   */
  suspend fun retractImageVote(imageCollectionId: String, imageUrl: String, userId: String): Boolean

  /**
   * Deletes all the images related to a user
   *
   * @param userId The user id to whom we delete all the data from. (pictures uploaded and ratings)
   */
  suspend fun deleteAllDataFromUser(userId: String)

  /**
   * Register a listener to a specific imageCollectionId
   *
   * @param imageCollectionId The id of the document to listen to.
   * @param onDocumentChange The callback to be called each time the document changes
   */
  fun registerCollectionListener(imageCollectionId: String, onDocumentChange: () -> Unit)
}
