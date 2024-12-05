package com.android.streetworkapp.model.image

import android.net.Uri
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.user.User

interface ImageRepository {
    /**
     * Uploads the image to the db
     * @param imageB64 the base64 representation of the image
     * @param park the park the image will be linked to
     * @param user the user that uploaded the image
     */
    suspend fun uploadImage(imageB64: String, park: Park, user: User)

    /**
     * Retrieves all the images from the park in base 64
     */
    suspend fun retrieveImages(park: Park): List<String>
}