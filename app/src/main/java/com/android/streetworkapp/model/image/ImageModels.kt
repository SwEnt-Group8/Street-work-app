package com.android.streetworkapp.model.image

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlin.math.log
import kotlin.math.pow

/**
 * A data class that represents a collection of images related to a park.
 *
 * @property id The id of the collection.
 * @property images A list of ParkImage
 */
data class ParkImageCollection(
    val id: String = "",
    val images: List<ParkImage> = emptyList()
) // note: if images field were to be changed, make sure to match the field name change in
// ImageRepositoryFirestore::uploadImage

data class ImageRating(val positiveVotes: Int = 0, val negativeVotes: Int = 0, val positiveVotesUids: List<String> = emptyList(), val negativeVotesUids: List<String> = emptyList()) {
    /**
     * Returns a computed value from the [Pair<Int, Int>] of the score.
     * It's a weighted score slowly increasing for the total number of votes, prioritizing the ratio of positive to negative reviews.
     */
    @Exclude //we don't want this to get serialized
    fun getImageScore(): Double {
        return log((this.positiveVotes + this.negativeVotes + 2).toDouble().pow(0.4), 2.0)*(this.positiveVotes+1)/(this.negativeVotes+1)
    }
}

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
    val imageUrl: String = "",
    val userId: String = "",
    val username: String = "",
    val rating: ImageRating = ImageRating(0, 0),
    val uploadDate: Timestamp = Timestamp.now()
)

enum class VOTE_TYPE(val value: Int) {
  POSITIVE(1),
  NEGATIVE(1)
}

