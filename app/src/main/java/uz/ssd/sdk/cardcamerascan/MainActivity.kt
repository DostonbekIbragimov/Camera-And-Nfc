package uz.ssd.sdk.cardcamerascan

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import uz.ssd.sdk.camerascan.CardScanActivity
import uz.ssd.sdk.nfc.NfcActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfc.setOnClickListener {
            nfcOpen()

        }

        camera.setOnClickListener {
            startActivity(Intent(this, CardScanActivity::class.java))
        }
    }

    private fun nfcOpen() {
        val mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (!mNfcAdapter.isEnabled) {
            val alertbox: AlertDialog.Builder = AlertDialog.Builder(this)
            alertbox.setTitle("Info")
            alertbox.setMessage(getString(R.string.msg_nfcon))
            alertbox.setPositiveButton("Turn On") { dialog, which ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                    startActivity(intent)
                } else {
                    val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(intent)
                }
            }
            alertbox.setNegativeButton("Close") { dialog, which ->

            }
            alertbox.show()
        } else {
            startActivity(Intent(this, NfcActivity::class.java))
        }
    }
}