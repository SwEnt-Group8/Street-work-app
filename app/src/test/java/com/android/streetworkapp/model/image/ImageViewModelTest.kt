package com.android.streetworkapp.model.image

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
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
  fun `sha256 returns correct hash on known value`() {
    val hash = imageViewModel.sha256("mybase64image")
    assert(hash == "f28cac1b38195259aa87ead50c4fa1411bf2c13c9b18c73a0ce5198b2c1a0cf9")
  }
}
