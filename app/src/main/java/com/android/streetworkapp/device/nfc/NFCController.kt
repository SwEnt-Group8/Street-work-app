package com.android.streetworkapp.device.nfc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import androidx.appcompat.app.AlertDialog
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.utils.DefaultToastNotifier

@SuppressLint("ServiceCast")
class NFCController(
    private val context: Context,
    private val userViewModel: UserViewModel,
    private val onUidReceived: (String) -> Unit,
    private val notifier: DefaultToastNotifier
) {

  private var nfcAdapter: NfcAdapter? = null

  init {
    nfcAdapter = context.getSystemService(Context.NFC_SERVICE) as? NfcAdapter
    if (nfcAdapter == null) notifier.showMessage("NFC not supported on this device")
  }

  fun enableNfcExchange() {
    if (nfcAdapter != null && nfcAdapter!!.isEnabled) {
      notifier.showMessage("Bring devices close to initiate NFC exchange")
    } else {
      notifier.showMessage("NFC exchange cannot be initiated")
    }
  }

  fun handleNfcIntent(intent: Intent, currentUserId: String) {
    val action = intent.action
    if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
      val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
      if (rawMessages == null || rawMessages.isEmpty()) {
        notifier.showMessage("No NFC data found")
        return
      }

      val messages = rawMessages.map { it as NdefMessage }
      val firstMessage = messages[0]

      if (firstMessage.records.isNotEmpty()) {
        val receivedUid = String(firstMessage.records[0].payload).substring(3)
        onUidReceived(receivedUid)
        showFriendRequestDialog(currentUserId, receivedUid)
      } else {
        notifier.showMessage("No NFC data found")
      }
    }
  }

  fun createFriendRequestDialog(
      context: Context,
      currentUserId: String,
      friendUid: String
  ): AlertDialog {
    return AlertDialog.Builder(context)
        .setTitle("Friend Request")
        .setMessage("Do you want to add this user as a friend?")
        .setPositiveButton("Accept") { _, _ ->
          userViewModel.addFriend(currentUserId, friendUid)
          notifier.showMessage("Friend added successfully")
        }
        .setNegativeButton("Refuse") { dialogInterface, _ ->
          dialogInterface.dismiss()
          notifier.showMessage("Friend request refused")
        }
        .create()
  }

  fun showFriendRequestDialog(currentUserId: String, friendUid: String) {
    val dialog = createFriendRequestDialog(context, currentUserId, friendUid)
    dialog.show()
  }
}
