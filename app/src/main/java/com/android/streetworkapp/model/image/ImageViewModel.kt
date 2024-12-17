package com.android.streetworkapp.model.image

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.streetworkapp.model.park.Park
import java.io.File
import java.util.UUID
import kotlinx.coroutines.launch

open class ImageViewModel(private val imageRepository: ImageRepository) : ViewModel() {
  /**
   * Uploads an image to our s3 storage and saves an entry with the url in our firebase db.
   *
   * @param context The current context.
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
      try {
        val uniqueIdentifier = UUID.randomUUID().toString()
        imageRepository.uploadImage(uniqueIdentifier, image.readBytes(), parkId, userId)
        onImageUploadSuccess()
      } catch (e: Exception) {
        onImageUploadFailure()
      }
    }
  }

  /**
   * Retrieves all the images from the park.
   *
   * @param context The current context.
   * @param park The [Park] which to retrieve images from.
   * @param imagesCallback A function that gets called with all the images as param.
   */
  open fun retrieveImages(context: Context, park: Park, imagesCallback: (List<ParkImage>) -> Unit) {
    viewModelScope.launch {
      val retrievedImages = imageRepository.retrieveImages(park)
      imagesCallback(retrievedImages)
    }
  }

  /**
   * Updates the ranking of the image.
   *
   * @param imageCollectionId The collection the image belongs to.
   * @param imageUrl The url of the image of whom to register the vote to.
   * @param voterUid The uid of the voter.
   * @param vote The vote type. True if a positive vote, false if a negative vote.
   */
  open fun imageVote(
      imageCollectionId: String,
      imageUrl: String,
      voterUid: String,
      vote: VOTE_TYPE
  ) {
    viewModelScope.launch { imageRepository.imageVote(imageCollectionId, imageUrl, voterUid, vote) }
  }

  /**
   * Removes the user's vote from the image
   *
   * @param imageCollectionId The collection id the image is part of.
   * @param imageUrl The url of the image.
   * @param userId The userId of the vote to remove.
   */
  open fun retractImageVote(imageCollectionId: String, imageUrl: String, userId: String) {
    viewModelScope.launch { imageRepository.retractImageVote(imageCollectionId, imageUrl, userId) }
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
   * Deletes all the images related to a user
   *
   * @param userId The user id to whom we delete all the data from. (pictures uploaded and ratings)
   */
  open fun deleteAllDataFromUser(
      userId: String,
      onDataDeletionSuccess: () -> Unit,
      onDataDeletionFailure: () -> Unit
  ) {
    viewModelScope.launch {
      if (imageRepository.deleteAllDataFromUser(userId)) onDataDeletionSuccess()
      else onDataDeletionFailure()
    }
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
