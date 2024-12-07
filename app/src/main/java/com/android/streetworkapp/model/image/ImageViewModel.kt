package com.android.streetworkapp.model.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.streetworkapp.model.park.Park
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

open class ImageViewModel(private val imageRepository: ImageRepository): ViewModel() {
    /**
     * Uploads an image in b64 format into our database.
     * @param context The current context.
     * @param imageUri The [Uri] of the image to be uploaded.
     * @param parkId The parkId the image will be linked to.
     * @param userId The id of the image uploader..
     * @param deleteImageAfterUpload Deletes the image after upload if true.
     */
    open suspend fun uploadImage(context: Context, imageUri: Uri, parkId: String, userId: String, deleteImageAfterUpload: Boolean) {
        viewModelScope.launch {
            val isImageUriValid = imageUri.path?.let { File(it).run { exists() && isFile() } } ?: false
            if (!isImageUriValid)
                return@launch

            val base64Image = uriToBase64(context, imageUri) ?: run { return@launch }
            imageRepository.uploadImage(base64Image, parkId, userId)
        }
    }

    /**
     * Retrieves all the images from the park, the images will be saved into in to the apps cache.
     * @param context The current context.
     * @param park The [Park] which to retrieve images from.
     * @param imagesCallback A function that gets called once all the images have been decoded and saved to disk. It takes the decoded list as parameter.
     */
    open suspend fun retrieveImages(context: Context, park: Park, imagesCallback: (List<ParkImageLocal>) -> Unit) {
        viewModelScope.launch {
            val retrievedImages = imageRepository.retrieveImages(park)
            val cacheDir = context.cacheDir
            val localParkImages = mutableListOf<ParkImageLocal>()
            for (parkImage in retrievedImages) {
                val imageHash = sha256(parkImage.imageB64)

                val decodedBytes = Base64.decode(parkImage.imageB64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                val imageFile = File(cacheDir, "$imageHash.jpg")
                if (imageFile.exists())
                    continue //Image already saved in cache, no need to decode it

                FileOutputStream(imageFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }

                val imageUri = Uri.fromFile(imageFile)
                val localParkImage = ParkImageLocal(
                    image = imageUri,
                    userId = parkImage.userId,
                    rating = parkImage.rating,
                    uploadDate = parkImage.uploadDate
                )

                localParkImages.add(localParkImage)
            }

            imagesCallback(localParkImages.toList())
        }
    }

    /**
     * Takes an URI and returns its base64 encoding.
     * @param context The current context.
     * @param imageUri The [Uri] of the image to encode.
     */
    private fun uriToBase64(context: Context, imageUri: Uri): String? {
        return context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            Base64.encodeToString(inputStream.readBytes(), Base64.DEFAULT)
        }
    }

    /**
     * Takes a string and returns its SHA-256 hash
     * @param string The [String to be hashed]
     */
    private fun sha256(string: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = digest.digest(string.toByteArray(Charsets.UTF_8))
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }
}