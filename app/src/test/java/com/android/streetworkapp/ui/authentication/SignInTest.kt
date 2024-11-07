import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.authentication.checkAndAddUser
import com.android.streetworkapp.ui.authentication.observeAndSetCurrentUser
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.kotlin.any
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class SignInTest {

  @get:Rule val mockitoRule = MockitoJUnit.rule()
  @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

  private lateinit var userViewModel: UserViewModel
  @Mock lateinit var repository: UserRepository
  private val testDispatcher = StandardTestDispatcher()
  @Mock private lateinit var firebaseUser: FirebaseUser
  private val fakeUserLiveData = MutableLiveData<User?>()

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    Dispatchers.setMain(testDispatcher)

    // Create a spy on UserViewModel and set the MutableLiveData directly
    userViewModel = spy(UserViewModel(repository))
    `when`(userViewModel.user).thenReturn(fakeUserLiveData)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun checkAndAddUser_withExistingUser_doesNotAddUser() = runTest {
    // Given a FirebaseUser and an existing user in the repository
    `when`(firebaseUser.uid).thenReturn("user123")
    `when`(firebaseUser.displayName).thenReturn("John Doe")
    `when`(firebaseUser.email).thenReturn("john@example.com")

    val existingUser = User("user123", "John Doe", "john@example.com", 100, emptyList())
    fakeUserLiveData.value = existingUser

    // Call the function
    checkAndAddUser(firebaseUser, userViewModel)

    // Advance the test dispatcher to execute the coroutine
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify that addUser is not called
    verify(repository, never()).addUser(any())
  }

  @Test
  fun checkAndAddUser_withNullUser_doesNothing() = runTest {
    // Call the function with a null user
    checkAndAddUser(null, userViewModel)

    // Verify that getUserByUid and addUser are not called
    verify(repository, never()).getUserByUid(any())
    verify(repository, never()).addUser(any())
  }

  @Test
  fun `observeAndSetCurrentUser adds new user if user does not exist`() = runTest {
    // Given a FirebaseUser and no user in the repository
    `when`(firebaseUser.uid).thenReturn("newUser123")
    `when`(firebaseUser.displayName).thenReturn("New User")
    `when`(firebaseUser.email).thenReturn("newuser@example.com")
    fakeUserLiveData.value = null // Simulate no user initially

    // Call the observeAndSetCurrentUser function
    observeAndSetCurrentUser(firebaseUser, userViewModel)

    val expectedUser =
        User(
            uid = "newUser123",
            username = "New User",
            email = "newuser@example.com",
            score = 0,
            friends = emptyList())

    // Verify that addUser and setCurrentUser were called with the expected user
    verify(userViewModel).addUser(expectedUser)
    verify(userViewModel).setCurrentUser(expectedUser)
  }

  @Test
  fun `observeAndSetCurrentUser sets existing user if user already exists`() = runTest {
    // Given a FirebaseUser and an existing user in the LiveData
    val existingUser = User("user123", "Existing User", "existing@example.com", 100, emptyList())
    fakeUserLiveData.value = existingUser
    `when`(firebaseUser.uid).thenReturn("user123")

    // Call the observeAndSetCurrentUser function
    observeAndSetCurrentUser(firebaseUser, userViewModel)

    // Verify that addUser was not called and setCurrentUser was called with the existing user
    verify(userViewModel, never()).addUser(any())
    verify(userViewModel).setCurrentUser(existingUser)
  }
}
