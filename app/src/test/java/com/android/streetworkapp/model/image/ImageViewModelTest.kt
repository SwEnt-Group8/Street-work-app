package com.android.streetworkapp.model.image

import android.content.Context
import android.net.Uri
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.user.User
import com.google.firebase.Timestamp
import java.io.File
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.RETURNS_DEFAULTS
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ImageViewModelTest {

  @Mock private lateinit var imageRepository: ImageRepository
  @Mock private lateinit var context: Context

  @get:Rule val temporaryFolder = TemporaryFolder()

  private lateinit var imageViewModel: ImageViewModel

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    imageViewModel = spy(ImageViewModel(imageRepository))
  }

  @Test
  fun `viewModel calls uploadImage from repository and onSuccessCallback`() = runBlocking {
    val mockCallback = mock(Runnable::class.java, RETURNS_DEFAULTS)
    doReturn("mybase64image").whenever(imageViewModel).uriToBase64(any(), any())
    whenever(imageRepository.uploadImage(any(), any(), any())).then {}

    imageViewModel.uploadImage(
        mock(Context::class.java),
        mock(Uri::class.java),
        "parkId",
        "userId",
        { mockCallback.run() },
        {})

    verify(imageRepository).uploadImage("mybase64image", "parkId", "userId")
    verify(mockCallback).run()
  }

  @Test
  fun `retrieveImages saves images in the cache by their hash name`() = runTest {
    val mockCacheFolder = temporaryFolder.newFolder()
    val park = Park(pid = "parkId")
    val userWhoUploadedImage = User("userId", "name", "mail", 10, emptyList(), "")
    // 1x1 jpg format pixel in base64, note: the images should all be in jpeg format for this test.
    val dummiesBase64 =
        listOf(
            "/9j/4AAQSkZJRgABAQEAYABgAAD/2wCEAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAf/CABEIAAEAAQMBIgACEQEDEQH/xAAUAAEAAAAAAAAAAAAAAAAAAAAK/9oACAEBAAAAAH8f/8QAFAEBAAAAAAAAAAAAAAAAAAAAAP/aAAgBAhAAAAB//8QAFAEBAAAAAAAAAAAAAAAAAAAAAP/aAAgBAxAAAAB//8QAFBABAAAAAAAAAAAAAAAAAAAAAP/aAAgBAQABPwB//8QAFBEBAAAAAAAAAAAAAAAAAAAAAP/aAAgBAgEBPwB//8QAFBEBAAAAAAAAAAAAAAAAAAAAAP/aAAgBAwEBPwB//9k=")
    val parkImagesDatabase =
        listOf(
            ParkImageDatabase(
                dummiesBase64[0], userWhoUploadedImage.uid, Pair(2, 0), Timestamp.now()))
    whenever(imageRepository.retrieveImages(park)).thenReturn(parkImagesDatabase)
    whenever(context.cacheDir).thenReturn(mockCacheFolder)

    imageViewModel.retrieveImages(context, park) { localParkImages ->
      assert(localParkImages.size == parkImagesDatabase.size)

      val parkFile = File(mockCacheFolder, park.pid)
      for ((index, parkImage) in localParkImages.withIndex()) {
        val imageHash = imageViewModel.sha256(dummiesBase64[index])
        // check that the file exists
        val imageFile = File(parkFile, "${imageHash}.jpg")
        assert(imageFile.exists())
        assert(parkImage.rating == parkImagesDatabase[index].rating)
        assert(parkImage.userId == parkImagesDatabase[index].userId)
        assert(parkImage.uploadDate == parkImagesDatabase[index].uploadDate)
      }
    }
  }

  @Test
  fun `sha256 returns correct hash on known value`() {
    val hash = imageViewModel.sha256("mybase64image")
    assert(hash == "f28cac1b38195259aa87ead50c4fa1411bf2c13c9b18c73a0ce5198b2c1a0cf9")
  }
}
