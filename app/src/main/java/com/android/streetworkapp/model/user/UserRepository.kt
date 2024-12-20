package com.android.streetworkapp.model.user
/** A repository interface for user data. */
interface UserRepository {

  fun getNewUid(): String

  suspend fun getUserByUid(uid: String): User?

  suspend fun getUsersByUids(uids: List<String>): List<User>?

  suspend fun getUserByEmail(email: String): User?

  suspend fun getUserByUserName(userName: String): User?

  suspend fun getFriendsByUid(uid: String): List<User>?

  suspend fun getParksByUid(uid: String): List<String>?

  suspend fun addUser(user: User)

  suspend fun getOrAddUserByUid(uid: String, user: User): User?

  suspend fun updateUserScore(uid: String, newScore: Int)

  suspend fun increaseUserScore(uid: String, points: Int)

  suspend fun addFriend(uid: String, friendUid: String)

  suspend fun removeFriend(uid: String, friendUid: String)

  suspend fun addNewPark(uid: String, parkId: String)

  suspend fun removeUserFromAllFriendsLists(uid: String)

  suspend fun deleteUserByUid(uid: String)
}
