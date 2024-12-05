package com.android.streetworkapp.model.image

import android.net.Uri
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.user.User

class ImageRepositoryFirestore(): ImageRepository {
    companion object {
        const val DEBUG_PREFIX = "ImageRepositoryFirestore:"
    }


    override suspend fun uploadImage(imageB64: String, park: Park, user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun retrieveImages(park: Park): List<String> {
        TODO("Not yet implemented")
    }

}