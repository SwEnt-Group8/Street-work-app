package com.android.streetworkapp.model.user

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

private const val s = "UID must not be empty"

class UserRepositoryFirestore(private val db: FirebaseFirestore) : UserRepository {
  // Setup the collection path
  companion object {
    private const val COLLECTION_PATH = "users"
  }

  /**
   * Generates a new unique ID for a user.
   *
   * @return A new unique ID (document ID) generated by Firestore.
   */
  override fun getNewUid(): String {
    return db.collection(COLLECTION_PATH).document().id
  }

  /**
   * Retrieves a user from Firestore based on the provided ID.
   *
   * @param uid The unique ID of the user to retrieve.
   * @return The User object if found, or null if the user doesn't exist or an error occurs.
   */
  override suspend fun getUserByUid(uid: String): User? {
    require(uid.isNotEmpty()) { "UID must not be empty" }
    return try {
      val document = db.collection("users").document(uid).get().await()
      documentToUser(document)
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error getting user with ID: $uid. Reason: ${e.message}")
      null
    }
  }

  /**
   * Retrieves a user from Firestore based on the provided email.
   *
   * @param email The email of the user to retrieve.
   * @return The User object if found, or null if the user doesn't exist or an error occurs.
   */
  override suspend fun getUserByEmail(email: String): User? {
    require(email.isNotEmpty()) { "Email must not be empty" }
    return try {
      val querySnapshot = db.collection(COLLECTION_PATH).whereEqualTo("email", email).get().await()

      if (querySnapshot.documents.isNotEmpty()) {
        documentToUser(querySnapshot.documents[0]) // Return the first match
      } else {
        null // No user with that email found
      }
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error getting user by email: $email. Reason: ${e.message}")
      null
    }
  }

  /**
   * Retrieves the friends of a user from Firestore based on the provided user ID (uid).
   *
   * @param uid The unique ID of the user whose friends are being retrieved.
   * @return A list of User objects representing the user's friends, or null if an error occurs.
   */
  override suspend fun getFriendsByUid(uid: String): List<User>? {
    require(uid.isNotEmpty()) { "UID must not be empty" }
    return try {
      // Get the user's document first to retrieve the list of friend UIDs
      val document = db.collection(COLLECTION_PATH).document(uid).get().await()
      val friendIds = document.get("friends") as? List<String> ?: emptyList()

      if (friendIds.isNotEmpty()) {
        // Now fetch all the friends' user documents
        val friendsQuery =
            db.collection(COLLECTION_PATH).whereIn(FieldPath.documentId(), friendIds).get().await()

        friendsQuery.documents.mapNotNull {
          documentToUser(it)
        } // Convert documents to User objects
      } else {
        emptyList() // No friends found
      }
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error getting friends for user ID: $uid. Reason: ${e.message}")
      null
    }
  }

  /**
   * Adds a new user to Firestore.
   *
   * @param user The User object to add to Firestore.
   */
  override suspend fun addUser(user: User) {
    require(user.uid.isNotEmpty()) { "User ID must not be empty" }
    try {
      db.collection("users").document(user.uid).set(user).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error adding user: ${e.message}")
    }
  }

  /**
   * Updates the user's score in Firestore.
   *
   * @param uid The unique ID of the user whose score is being updated.
   * @param newScore The new score to set for the user.
   */
  override suspend fun updateUserScore(uid: String, newScore: Int) {
    require(uid.isNotEmpty()) { "UID must not be empty" }
    require(newScore >= 0) { "Score must be a non-negative integer" }
    try {
      db.collection("users").document(uid).update("score", newScore).await()
    } catch (e: Exception) {
      Log.e(
          "FirestoreError", "Error updating score of the user with ID: $uid. Reason: ${e.message}")
    }
  }

  /**
   * Increases the user's score in Firestore by a specified number of points.
   *
   * @param uid The unique ID of the user whose score is being increased.
   * @param points The number of points to add to the user's score.
   */
  override suspend fun increaseUserScore(uid: String, points: Int) {
    require(uid.isNotEmpty()) { "UID must not be empty" }
    require(points >= 0) { "Points must be a non-negative integer" }
    try {
      db.collection("users")
          .document(uid)
          .update("score", FieldValue.increment(points.toLong()))
          .await()
    } catch (e: Exception) {
      Log.e(
          "FirestoreError",
          "Error increasing score of the user with ID: $uid. Reason: ${e.message}")
    }
  }

  /**
   * Adds a friend to both the user's and friend's friend lists in Firestore.
   *
   * @param uid The unique ID of the user.
   * @param friendUid The ID of the friend to add to the user's friend list.
   */
  override suspend fun addFriend(uid: String, friendUid: String) {
    require(uid.isNotEmpty()) { "UID must not be empty" }
    require(friendUid.isNotEmpty()) { "Friend UID must not be empty" }
    try {
      // Start a Firestore batch operation for both updates
      val batch = db.batch()

      // Add friendId to the user's friends list
      val userRef = db.collection("users").document(uid)
      batch.update(userRef, "friends", FieldValue.arrayUnion(friendUid))

      // Add uid to the friend's friends list and commit the batch
      val friendRef = db.collection("users").document(friendUid)
      batch.update(friendRef, "friends", FieldValue.arrayUnion(uid))
      batch.commit().await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error adding friend: ${e.message}")
    }
  }

  /**
   * Removes a friend from the user's friend list in Firestore.
   *
   * @param uid The unique ID of the user.
   * @param friendUid The ID of the friend to remove from the user's friend list.
   */
  override suspend fun removeFriend(uid: String, friendUid: String) {
    require(uid.isNotEmpty()) { "UID must not be empty" }
    require(friendUid.isNotEmpty()) { "Friend UID must not be empty" }
    try {
      // Start a Firestore batch operation for both removals
      val batch = db.batch()

      // Remove friendId from the user's friends list
      val userRef = db.collection("users").document(uid)
      batch.update(userRef, "friends", FieldValue.arrayRemove(friendUid))

      // Remove uid from the friend's friends list and commit the batch
      val friendRef = db.collection("users").document(friendUid)
      batch.update(friendRef, "friends", FieldValue.arrayRemove(uid))
      batch.commit().await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error removing friend: ${e.message}")
    }
  }

  /**
   * Deletes a user from Firestore based on the provided ID.
   *
   * @param id The unique ID of the user to delete.
   */
  override suspend fun deleteUserById(id: String) {
    require(id.isNotEmpty()) { "ID must not be empty" }
    try {
      db.collection("users").document(id).delete().await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error deleting user: ${e.message}")
    }
  }

  /**
   * Converts a Firestore DocumentSnapshot into a User object.
   *
   * @param document The Firestore DocumentSnapshot containing user data.
   * @return A User object if the document is valid, or null if the document cannot be converted.
   */
  internal fun documentToUser(document: DocumentSnapshot): User? {
    return try {
      val uid = document.id
      val name = document.getString("name") ?: return null
      val email = document.getString("email") ?: return null
      val score = document.getLong("score")?.toInt() ?: 0

      val friends =
          try {
            document.get("friends") as? List<String> ?: emptyList()
          } catch (e: Exception) {
            Log.e("FirestoreError", "Error casting friends list", e)
            emptyList()
          }

      User(uid = uid, username = name, email = email, score = score, friends = friends)
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error converting document to User", e)
      null
    }
  }
}
