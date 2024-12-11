package com.android.streetworkapp.model.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.streetworkapp.model.park.Park
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import kotlinx.coroutines.launch

open class ImageViewModel(private val imageRepository: ImageRepository) : ViewModel() {
  /**
   * Uploads an image in b64 format into our database.
   *
   * @param context The current context.
   * @param imageUri The [Uri] of the image to be uploaded.
   * @param parkId The parkId the image will be linked to.
   * @param userId The id of the image uploader.
   * @param onImageUploadSuccess Callback to be called on upload success.
   * @param onImageUploadFailure Callback to be called on upload failure.
   */
  open fun uploadImage(
      context: Context,
      imageUri: Uri,
      parkId: String,
      userId: String,
      onImageUploadSuccess: () -> Unit,
      onImageUploadFailure: () -> Unit
  ) {
    viewModelScope.launch {
      // I'm just going to assume the imageUri is valid, would need to use contentResolver etc...
      val base64Image =
          uriToBase64(context, imageUri)
              ?: run {
                onImageUploadFailure()
                return@launch
              }
      try {
        val base64ImageHash = sha256(base64Image)
        imageRepository.uploadImage(base64ImageHash, base64Image, parkId, userId)
        onImageUploadSuccess()
      } catch (e: Exception) {
        onImageUploadFailure() // Not going to bother explaining the exception in the UI
      }
    }
  }

  /**
   * Retrieves all the images from the park, the images will be saved into in to the apps cache.
   *
   * @param context The current context.
   * @param park The [Park] which to retrieve images from.
   * @param imagesCallback A function that gets called once all the images have been decoded and
   *   saved to disk. It takes the decoded list as parameter.
   */
  open fun retrieveImages(
      context: Context,
      park: Park,
      imagesCallback: (List<ParkImageLocal>) -> Unit
  ) {
    viewModelScope.launch {
      val retrievedImages = imageRepository.retrieveImages(park)
      val cacheDir = context.cacheDir
      val localParkImages = mutableListOf<ParkImageLocal>()

      val parkFolder = File(cacheDir, park.pid)
      if (!parkFolder.exists()) {
        parkFolder.mkdirs()
      }

      for (parkImage in retrievedImages) {

        val decodedBytes = Base64.decode(parkImage.imageB64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        val imageFile = File(parkFolder, "${parkImage.imageHash}.jpg")
        if (!imageFile.exists()) { // image doesn't exist, we decode it
          FileOutputStream(imageFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
          }
        }

        val imageUri = Uri.fromFile(imageFile)
        val localParkImage =
            ParkImageLocal(
                imageHash = parkImage.imageHash,
                image = imageUri,
                userId = parkImage.userId,
                //rating = parkImage.rating,
                rating = Pair(0, 0),
                username = parkImage.username,
                uploadDate = parkImage.uploadDate)

        localParkImages.add(localParkImage)
      }

      imagesCallback(localParkImages.toList())
    }
  }

  open fun imageVote(        imageCollectionId: String,
                             imageHash: String,
                             voteType: Boolean) {
      viewModelScope.launch {
          imageRepository.imageVote(imageCollectionId, imageHash, voteType)
      }
  }

    /**
     * Deletes the image associated with the hash
     * @param imageCollectionId The document id that the images is in.
     * @param imageHash The hash of the base64 representation of the image.
     * @param onImageDeleteSuccess Callback for deletion success.
     * @param onImageDeleteFailure Callback for deletion failure.
     */

    open fun deleteImage(imageCollectionId: String, imageHash: String, onImageDeleteSuccess: () -> Unit, onImageDeleteFailure: () -> Unit) {
        viewModelScope.launch {
            if (imageRepository.deleteImage(imageCollectionId, imageHash))
                onImageDeleteSuccess()
            else
                onImageDeleteFailure()
        }
    }

  /**
   * Takes an URI and returns its base64 encoding.
   *
   * @param context The current context.
   * @param imageUri The [Uri] of the image to encode.
   */
  open fun uriToBase64(context: Context, imageUri: Uri): String? {
    return context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
      Base64.encodeToString(inputStream.readBytes(), Base64.DEFAULT)
    }
  }

  /**
   * Takes a string and returns its SHA-256 hash
   *
   * @param string The [String to be hashed]
   */
  open fun sha256(string: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashedBytes = digest.digest(string.toByteArray(Charsets.UTF_8))
    return hashedBytes.joinToString("") { "%02x".format(it) }
  }

    /**
     * Registers a callback that gets called each time the document gets updated
     * @param imageCollectionId The id of the document to listen to.
     * @param onCollectionUpdate The callback
     */
    open fun registerCollectionListener(imageCollectionId: String, onCollectionUpdate: () -> Unit) {
        viewModelScope.launch {
            require(imageCollectionId.isNotEmpty()) { "Empty imageCollectionId" }
            imageRepository.registerCollectionListener(imageCollectionId, onCollectionUpdate)
        }
    }
}
