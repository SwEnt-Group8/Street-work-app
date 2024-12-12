package com.android.streetworkapp.model.image

import com.google.firebase.Timestamp

/**
 * A data class that represents a collection of images related to a park.
 *
 * @property id The id of the collection.
 * @property images A list of ParkImage
 */
data class ParkImageCollection(
    val id: String,
    val images: List<ParkImage>
) // note: if images field were to be changed, make sure to match the field name change in
// ImageRepositoryFirestore::uploadImage

/**
 * A data class that represents an image related to a park in the database, uploaded by a user, and
 * associated with a rating.
 *
 * @property userId The id of the user who uploaded the image.
 * @property username The username associated to the userId
 * @property rating A pair of integers representing the rating, where the first element represents
 *   all the positive ratings and the second the negative ones.
 * @property uploadDate The date the image was uploaded
 */
// * Note: for the rating I would have used an unsigned int but there isn't a built in serializer
// for it and I can't be bothered
data class ParkImage(
    val imageUrl: String,
    val userId: String,
    val username: String,
    val rating: Pair<Int, Int> = Pair(0, 0),
    val uploadDate: Timestamp = Timestamp.now()
)
