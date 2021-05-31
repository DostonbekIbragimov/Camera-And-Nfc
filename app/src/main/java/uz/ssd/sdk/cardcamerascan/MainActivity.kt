package uz.ssd.sdk.cardcamerascan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import uz.ssd.sdk.camerascan.CardScanActivity
import uz.ssd.sdk.nfc.NfcActivity

//import uz.ssd.sdk.nfc.NFCActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfc.setOnClickListener {
            startActivity(Intent(this, NfcActivity::class.java))
        }

       /* camera.setOnClickListener {
            startActivity(Intent(this, CardScanActivity::class.java))
        }*/
    }
}