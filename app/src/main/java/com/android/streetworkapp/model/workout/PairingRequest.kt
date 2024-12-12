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
    val requestId: String = "",
    val fromUid: String = "",
    val toUid: String = "",
    val sessionId: String? = null,
    val status: RequestStatus = RequestStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)

/** An enum class representing the status of a pairing request. */
enum class RequestStatus {
  PENDING, // Waiting for the recipient's response
  ACCEPTED, // The request has been accepted
  REJECTED, // The request has been rejected
  OLD // The request is no longer valid
}
