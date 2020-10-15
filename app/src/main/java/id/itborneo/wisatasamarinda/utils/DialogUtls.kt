package id.itborneo.wisatasamarinda.utils

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class DialogUtls(private val context: Context) {
    fun setDialogNoInternet(retry: (() -> Unit)) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setTitle("Tidak Ada Internet")
        builder.setMessage("Wisata Samarinda membutuhkan internet. Silahkan Aktifkan Koneksi Internet dan tekan Retry.")

        builder.setPositiveButton(
            "Retry"
        ) { dialog, _ ->
            dialog.dismiss()
            retry()
        }
        val dialog: AlertDialog = builder.create() // calling builder.create after adding buttons

        dialog.show()
        Toast.makeText(context, "Network Unavailable!", Toast.LENGTH_LONG).show()
    }

    fun internetAvailable() {
//        Toast.makeText(context, "Network Available!!", Toast.LENGTH_LONG).show()

    }
}