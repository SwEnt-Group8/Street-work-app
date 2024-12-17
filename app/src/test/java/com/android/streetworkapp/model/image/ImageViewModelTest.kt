package com.android.streetworkapp.model.image

import android.content.Context
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.RETURNS_DEFAULTS
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
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
    whenever(imageRepository.uploadImage(any(), any(), any(), any())).then {}

    val mockFile = temporaryFolder.newFile()
    val mockData = ByteArray(10) { it.toByte() }
    mockFile.writeBytes(mockData)

    imageViewModel.uploadImage(mock(), mockFile, "parkId", "userId", { mockCallback.run() }, {})

    verify(imageRepository).uploadImage(any(), eq(mockData), eq("parkId"), eq("userId"))
    verify(mockCallback).run()
  }
}
