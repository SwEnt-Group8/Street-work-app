package com.android.streetworkapp.model.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class UserViewModel(private val repository: UserRepository) : ViewModel() {

  // MutableStateFlow to hold the current values
  private val _currentUser = MutableStateFlow<User?>(null)
  val currentUser: StateFlow<User?>
    get() = _currentUser

  private val _user = MutableStateFlow<User?>(null)
  val user: StateFlow<User?>
    get() = _user

  private val _friends = MutableStateFlow<List<User?>>(emptyList())
  val friends: StateFlow<List<User?>>
    get() = _friends

  private val _userList = MutableStateFlow<List<User?>>(emptyList())
  val userList: StateFlow<List<User?>>
    get() = _userList

  private val _parks = MutableStateFlow<List<String>>(emptyList())
  val parks: StateFlow<List<String>>
    get() = _parks

  /**
   * Sets the user to the provided User object.
   *
   * @param user The User object to set as the user.
   */
  fun setUser(user: User?) {
    _user.value = user
  }

  /**
   * Sets the current user to the provided User object.
   *
   * @param user The User object to set as the current user.
   */
  fun setCurrentUser(user: User?) {
    _currentUser.value = user
  }

  /**
   * Loads the current user from Firestore based on the provided user.
   *
   * @param user The user to load.
   */
  fun loadCurrentUser(user: User?) {
    if (user != null) {
      require(user.uid.isNotEmpty()) { "UID must not be empty" }
    }
    viewModelScope.launch { _currentUser.value = user }
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
  fun getUserByUid(uid: String) {
    viewModelScope.launch {
      val user = repository.getUserByUid(uid)
      if (user != null) {
        _user.value = user
      }
    }
  }

  /**
   * Retrieves a user from Firestore based on the provided uid and set it as the current user.
   *
   * @param uid The unique ID of the user to retrieve.
   * @return The User object if found, or null if the user doesn't exist or an error occurs.
   */
  fun getUserByUidAndSetAsCurrentUser(uid: String) {
    viewModelScope.launch {
      val user = repository.getUserByUid(uid)
      if (user != null) {
        _currentUser.value = user
      }
    }
  }

  /**
   * Retrieves a user from Firestore based on the provided email.
   *
   * @param email The email of the user to retrieve.
   * @return The User object if found, or null if the user doesn't exist or an error occurs.
   */
  fun getUserByEmail(email: String) {
    viewModelScope.launch {
      val fetchedUser = repository.getUserByEmail(email)
      _user.value = fetchedUser
    }
  }

  /**
   * Retrieves a user from Firestore based on the provided username.
   *
   * @param userName The username of the user to retrieve.
   * @return A list of users if found, or null if the user doesn't exist or an error occurs.
   */
  fun getUserByUserName(userName: String) {
    viewModelScope.launch {
      val fetchedUser = repository.getUserByUserName(userName)
      _user.value = fetchedUser
    }
  }

  /**
   * Retrieves a list of friends for a user based on the provided ID.
   *
   * @param uid The unique ID of the user to retrieve friends for.
   * @return A list of User objects representing the user's friends.
   */
  fun getFriendsByUid(uid: String) {
    viewModelScope.launch {
      val fetchedUsers = repository.getFriendsByUid(uid)
      if (fetchedUsers != null) {
        _friends.value = fetchedUsers
      }
    }
  }

  /**
   * Retrieves a list of users from Firestore based on the provided list of IDs.
   *
   * @param uids The list of unique IDs to retrieve users for.
   * @return A list of User objects representing the users.
   */
  fun getUsersByUids(uids: List<String>) {
    viewModelScope.launch {
      val fetchedUsers = repository.getUsersByUids(uids)
      if (fetchedUsers != null) {
        _userList.value = fetchedUsers
      }
    }
  }

  /**
   * Retrieves the parks of a user from Firestore based on the provided user ID (uid).
   *
   * @param uid The unique ID of the user whose friends are being retrieved.
   * @return A list of ID of park visited by the user, or null if an error occurs.
   */
  fun getParksByUid(uid: String) {
    viewModelScope.launch {
      val fetchedParks = repository.getParksByUid(uid)
      if (fetchedParks != null) {
        _parks.value = fetchedParks
      }
    }
  }

  /**
   * Adds a new user to Firestore.
   *
   * @param user The User object to add to Firestore.
   */
  fun addUser(user: User) = viewModelScope.launch { repository.addUser(user) }

  /**
   * Retrieves a user from Firestore based on the provided ID, or adds it if they don't exist.
   *
   * @param uid The unique ID of the user to retrieve or add.
   * @param user The User object to add if the user doesn't exist.
   */
  fun getOrAddUserByUid(uid: String, user: User) {
    viewModelScope.launch {
      val fetchedUser = repository.getOrAddUserByUid(uid, user)
      _user.value = fetchedUser
    }
  }

  /**
   * Updates the user's score in Firestore.
   *
   * @param uid The unique ID of the user whose score is being updated.
   * @param newScore The new score to set for the user.
   */
  fun updateUserScore(uid: String, newScore: Int) =
      viewModelScope.launch { repository.updateUserScore(uid, newScore) }

  /**
   * Increases the user's score in Firestore by the specified number of points.
   *
   * @param uid The unique ID of the user whose score is being increased.
   * @param points The number of points to add to the user's score.
   */
  fun increaseUserScore(uid: String, points: Int) =
      viewModelScope.launch {
        repository.increaseUserScore(uid, points)

        _currentUser.value?.let {
          val updatedUser = it.copy(score = it.score + points)
          _currentUser.value = updatedUser
        }
      }

  /**
   * Adds a friend to both the user's and friend's friend lists in Firestore.
   *
   * @param uid The unique ID of the user.
   * @param friendUid The ID of the friend to add to the user's friend list.
   */
  fun addFriend(uid: String, friendUid: String) =
      viewModelScope.launch { repository.addFriend(uid, friendUid) }

  /**
   * Removes a friend from the user's friend list in Firestore.
   *
   * @param uid The unique ID of the user.
   * @param friendUid The ID of the friend to remove from the user's friend list.
   */
  fun removeFriend(uid: String, friendUid: String) =
      viewModelScope.launch { repository.removeFriend(uid, friendUid) }

  /**
   * Adds the newly discovered park in the parks lists in Firestore.
   *
   * @param uid The unique ID of the user.
   * @param parkId The ID of the park to add to the user's parks list.
   */
  fun addNewPark(uid: String, parkId: String) =
      viewModelScope.launch { repository.addNewPark(uid, parkId) }

  /**
   * Removes a user from all friends lists in Firestore.
   *
   * @param uid The unique ID of the user to remove from all friends lists.
   */
  fun removeUserFromAllFriendsLists(uid: String) =
      viewModelScope.launch { repository.removeUserFromAllFriendsLists(uid) }

  /**
   * Deletes a user from Firestore based on the provided ID.
   *
   * @param uid The unique ID of the user to delete.
   */
  fun deleteUserByUid(uid: String) = viewModelScope.launch { repository.deleteUserByUid(uid) }
}
