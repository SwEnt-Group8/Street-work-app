package com.android.streetworkapp.model.workout

/**
 * A data class representing a pairing request between users.
 *
 * @param requestId Unique ID of the pairing request (generated by the database or app).
 * @param fromUid UID of the user sending the request.
 * @param toUid UID of the user receiving the request (e.g., a potential coach).
 * @param status The current status of the request (e.g., PENDING, ACCEPTED, REJECTED).
 * @param timestamp The time when the request was created, in epoch milliseconds.
 */
data class PairingRequest(
    val requestId: String = "", // Unique ID for the request
    val fromUid: String = "", // User who sent the request
    val toUid: String = "", // User who received the request
    val status: RequestStatus = RequestStatus.PENDING, // Current status of the request
    val timestamp: Long = System.currentTimeMillis() // Time of creation
)

/** An enum class representing the status of a pairing request. */
enum class RequestStatus {
  PENDING, // Waiting for the recipient's response
  ACCEPTED, // The request has been accepted
  REJECTED // The request has been rejected
}
