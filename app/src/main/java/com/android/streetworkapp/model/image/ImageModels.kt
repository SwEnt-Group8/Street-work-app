package com.android.streetworkapp.model.image

import android.net.Uri
import com.google.firebase.Timestamp

/**
 * A data class that represents a collection of images related to a park.
 *
 * @property id The id of the collection.
 * @property images A list of ParkImage
 */
data class ParkImageCollection(
    val id: String,
    val images: List<ParkImageDatabase>
) // note: if images field were to be changed, make sure to match the field name change in
  // ImageRepositoryFirestore::uploadImage

/**
 * A data class that represents an image related to a park in the database, uploaded by a user, and
 * associated with a rating.
 *
 * @property imageB64 The image in Base64 string format.
 * @property userId The id of the user who uploaded the image.
 * @property rating A pair of integers representing the rating, where the first element represents
 *   all the positive ratings and the second the negative ones.
 * @property uploadDate The date the image was uploaded
 */
// * Note: for the rating I would have used an unsigned int but there isn't a built in serializer
// for it and I can't be bothered
data class ParkImageDatabase(
    val imageB64: String,
    val userId: String,
    val rating: Pair<Int, Int> = Pair(0, 0),
    val uploadDate: Timestamp = Timestamp.now()
)

/**
 * A data class that represents an image related to a park in the app, uploaded by a user, and
 * associated with a rating.
 *
 * @property image The [Uri] of the image.
 * @property userId The id of the user who uploaded the image.
 * @property rating A pair of integers representing the rating, where the first element represents
 *   all the positive ratings and the second the negative ones.
 * @property uploadDate The date the image was uploaded
 */
data class ParkImageLocal(
    val image: Uri,
    val userId: String,
    val rating: Pair<Int, Int> = Pair(0, 0),
    val uploadDate: Timestamp = Timestamp.now()
)
