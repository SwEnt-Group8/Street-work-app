package com.android.streetworkapp.model.image

import com.android.streetworkapp.model.park.Park

interface ImageRepository {
  /**
   * Uploads the image to the db.
   *
   * @param imageB64 the base64 representation of the image.
   * @param parkId the id of the park the image will be linked to.
   * @param userId the id of the user that uploaded the image.
   */
  suspend fun uploadImage(imageB64: String, parkId: String, userId: String)

  /** Retrieves all the images linked from the park. */
  suspend fun retrieveImages(park: Park): List<ParkImageDatabase>
}
