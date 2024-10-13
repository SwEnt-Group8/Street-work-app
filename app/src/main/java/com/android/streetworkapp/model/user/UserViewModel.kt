package com.android.streetworkapp.model.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

open class UserViewModel(private val repository: UserRepository) : ViewModel() {

  // LiveData to hold the current user
  private val _currentUser = MutableLiveData<User?>()
  val currentUser: LiveData<User?>
    get() = _currentUser

  /**
   * Loads the current user from Firestore based on the provided ID.
   *
   * @param uid The unique ID of the user to load.
   */
  fun loadCurrentUser(uid: String) {
    viewModelScope.launch {
      val user = repository.getUserByUid(uid)
      _currentUser.postValue(user)
    }
  }

  // Companion object to provide a factory for UserViewModel
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Create the UserRepository with the required dependencies
            val repository = UserRepositoryFirestore(FirebaseFirestore.getInstance())
            return UserViewModel(repository) as T
          }
        }
  }

  /**
   * Generates a new unique ID for a user.
   *
   * @return A new unique ID (document ID) generated by Firestore.
   */
  fun getNewUid(): String {
    return repository.getNewUid()
  }

  /**
   * Retrieves a user from Firestore based on the provided ID.
   *
   * @param uid The unique ID of the user to retrieve.
   * @return The User object if found, or null if the user doesn't exist or an error occurs.
   */
  suspend fun getUserById(uid: String) {
    repository.getUserByUid(uid)
  }

  /**
   * Retrieves a user from Firestore based on the provided email.
   *
   * @param email The email of the user to retrieve.
   * @return The User object if found, or null if the user doesn't exist or an error occurs.
   */
  suspend fun getUserByEmail(email: String) {
    repository.getUserByEmail(email)
  }

  /**
   * Retrieves a list of friends for a user based on the provided ID.
   *
   * @param uid The unique ID of the user to retrieve friends for.
   * @return A list of User objects representing the user's friends.
   */
  suspend fun getFriendsByUid(uid: String) {
    repository.getFriendsByUid(uid)
  }

  /**
   * Adds a new user to Firestore.
   *
   * @param user The User object to add to Firestore.
   */
  suspend fun addUser(user: User) {
    repository.addUser(user)
  }

  /**
   * Updates the user's score in Firestore.
   *
   * @param uid The unique ID of the user whose score is being updated.
   * @param newScore The new score to set for the user.
   */
  suspend fun updateUserScore(uid: String, newScore: Int) {
    repository.updateUserScore(uid, newScore)
  }

  /**
   * Increases the user's score in Firestore by the specified number of points.
   *
   * @param uid The unique ID of the user whose score is being increased.
   * @param points The number of points to add to the user's score.
   */
  suspend fun increaseUserScore(uid: String, points: Int) {
    repository.increaseUserScore(uid, points)
  }

  /**
   * Adds a friend to both the user's and friend's friend lists in Firestore.
   *
   * @param uid The unique ID of the user.
   * @param friendUid The ID of the friend to add to the user's friend list.
   */
  suspend fun addFriend(uid: String, friendUid: String) {
    repository.addFriend(uid, friendUid)
  }

  /**
   * Removes a friend from the user's friend list in Firestore.
   *
   * @param uid The unique ID of the user.
   * @param friendUid The ID of the friend to remove from the user's friend list.
   */
  suspend fun removeFriend(uid: String, friendUid: String) {
    repository.removeFriend(uid, friendUid)
  }

  /**
   * Deletes a user from Firestore based on the provided ID.
   *
   * @param id The unique ID of the user to delete.
   */
  suspend fun deleteUserById(id: String) {
    repository.deleteUserById(id)
  }
}
