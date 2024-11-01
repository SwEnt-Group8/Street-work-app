package com.android.streetworkapp.device.nfc

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.utils.DefaultToastNotifier
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.*

class NFCControllerTest {

  private lateinit var nfcController: NFCController
  private lateinit var mockNfcController: NFCController
  private lateinit var mockContext: Context
  private lateinit var mockUserViewModel: UserViewModel
  private lateinit var mockNfcAdapter: NfcAdapter
  private lateinit var mockIntent: Intent
  private lateinit var mockToastNotifier: DefaultToastNotifier

  @Before
  fun setup() {
    mockContext = mock()
    mockUserViewModel = mock()
    mockNfcAdapter = mock()
    mockIntent = mock()
    mockToastNotifier = mock()

    `when`(mockContext.getSystemService(Context.NFC_SERVICE)).thenReturn(mockNfcAdapter)
    `when`(mockNfcAdapter.isEnabled).thenReturn(true)
    nfcController =
        NFCController(
            context = mockContext,
            userViewModel = mockUserViewModel,
            onUidReceived = { /* Callback */},
            notifier = mockToastNotifier)

    mockNfcController = mock()
  }

  @Test
  fun `should show NFC not supported toast if nfcAdapter is null`() {
    // Simulate device without NFC support
    `when`(mockContext.getSystemService(Context.NFC_SERVICE)).thenReturn(null)
    nfcController =
        NFCController(
            context = mockContext,
            userViewModel = mockUserViewModel,
            onUidReceived = { /* Callback */},
            notifier = mockToastNotifier)
    verify(mockToastNotifier, times(1)).showMessage("NFC not supported on this device")
  }

  @Test
  fun `should show toast when NFC is enabled for NFC exchange`() {
    `when`(mockNfcAdapter.isEnabled).thenReturn(true)

    nfcController.enableNfcExchange()

    verify(mockToastNotifier).showMessage("Bring devices close to initiate NFC exchange")
  }

  @Test
  fun `should show toast to enable NFC when NFC is disabled`() {
    `when`(mockNfcAdapter.isEnabled).thenReturn(false)
    nfcController.enableNfcExchange()

    verify(mockToastNotifier).showMessage("NFC exchange cannot be initiated")
  }

  @Test
  fun `should show message when no NFC data is found`() {
    // Arrange
    val currentUserId = "current_user_id"
    `when`(mockIntent.action).thenReturn(NfcAdapter.ACTION_NDEF_DISCOVERED)
    `when`(mockIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES))
        .thenReturn(null) // No messages

    nfcController.handleNfcIntent(mockIntent, currentUserId)

    verify(mockToastNotifier).showMessage("No NFC data found")
  }
}
