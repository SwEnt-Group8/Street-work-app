package com.android.streetworkapp.model.image

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.streetworkapp.model.park.Park
import java.io.File
import java.security.MessageDigest
import java.util.UUID
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
      image: File,
      parkId: String,
      userId: String,
      onImageUploadSuccess: () -> Unit,
      onImageUploadFailure: () -> Unit
  ) {
    viewModelScope.launch {
      // I'm just going to assume the imageUri is valid, would need to use contentResolver etc...

      try {
        val uniqueIdentifier = UUID.randomUUID().toString()
        imageRepository.uploadImage(uniqueIdentifier, image.readBytes(), parkId, userId)
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
  open fun retrieveImages(context: Context, park: Park, imagesCallback: (List<ParkImage>) -> Unit) {
    viewModelScope.launch {
      val retrievedImages = imageRepository.retrieveImages(park)
      imagesCallback(retrievedImages)
    }
  }

  open fun imageVote(imageCollectionId: String, imageHash: String, vote: VOTE_TYPE) {
    viewModelScope.launch { imageRepository.imageVote(imageCollectionId, imageHash, vote) }
  }

  /**
   * Deletes the image associated with the hash
   *
   * @param imageCollectionId The document id that the images is in.
   * @param imageUrl The url of the image.
   * @param onImageDeleteSuccess Callback for deletion success.
   * @param onImageDeleteFailure Callback for deletion failure.
   */
  open fun deleteImage(
      imageCollectionId: String,
      imageUrl: String,
      onImageDeleteSuccess: () -> Unit,
      onImageDeleteFailure: () -> Unit
  ) {
    viewModelScope.launch {
      if (imageRepository.deleteImage(imageCollectionId, imageUrl)) onImageDeleteSuccess()
      else onImageDeleteFailure()
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
   *
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
