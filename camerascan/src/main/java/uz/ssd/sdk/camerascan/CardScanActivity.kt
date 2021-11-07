package uz.ssd.sdk.camerascan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lucem.anb.characterscanner.Scanner
import com.lucem.anb.characterscanner.ScannerListener
import kotlinx.android.synthetic.main.activity_card_scan.*


class CardScanActivity : AppCompatActivity() {

    var scanner: Scanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_card_scan)

        try {
            initScanner()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.camera_error), Toast.LENGTH_SHORT).show()
            closePage()
        }

    }

    private fun closePage() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        try {
            scanner?.scan()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.camera_error), Toast.LENGTH_SHORT).show()
            closePage()
        }
    }

    private fun initScanner() {
        val surfaceView = surface
        surfaceView.setOnDetectedListener(this, object : ScannerListener {
            override fun onDetected(detections: String) {
                var cardNumber = ""
                var cardExpire = ""
                cardNumber = basicCard(detections)
                    ?: (uzCard(detections) ?: (humoCard(detections) ?: (unionCard(detections)
                        ?: (attoCard(detections) ?: ""))))
                if (cardNumber.length > 5) {
                    cardExpire = expireDate(detections) ?: ""
                    if (cardExpire.isEmpty()) cardExpire = expireDate(detections) ?: ""
                    val returnIntent = Intent()
                    Log.d(CardScanActivity::class.java.toString(),
                        "card_number = $cardNumber\ncard_expire = $cardExpire")
                    returnIntent.putExtra("card_number", cardNumber.replace(" ", ""))
                    returnIntent.putExtra("card_expire", cardExpire)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            }
            override fun onStateChanged(state: String, i: Int) {
                Log.e("TTT", "state = $state")
            }
        })
        /*
        scanner = Scanner(this, surfaceView, object : ScannerListener {
            override fun onDetected(detections: String) {
                var cardNumber = ""
                var cardExpire = ""
                cardNumber = basicCard(detections)
                    ?: (uzCard(detections) ?: (humoCard(detections) ?: (unionCard(detections)
                        ?: (attoCard(detections) ?: ""))))
                if (cardNumber.length > 5) {
                    cardExpire = expireDate(detections) ?: ""
                    if (cardExpire.isEmpty()) cardExpire = expireDate(detections) ?: ""
                    val returnIntent = Intent()
                    Toast.makeText(
                        this@CardScanActivity,
                        "card_number = $cardNumber\ncard_expire = $cardExpire",
                        Toast.LENGTH_SHORT
                    ).show()
                    returnIntent.putExtra("card_number", cardNumber.replace(" ", ""))
                    returnIntent.putExtra("card_expire", cardExpire)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            }

            override fun onStateChanged(state: String, i: Int) {
                Log.e("TTT", "state = $state")
            }
        })*/
    }

    /* private fun openAlertDialog(cardNumber: String, cardExpire: String) {
         val adb = AlertDialog.Builder(this)
         adb.setTitle(R.string.is_right_scan)
         adb.setMessage(resources.getString(R.string.you_card) + cardNumber)
         adb.setPositiveButton(R.string.ok) { p0, p1 ->

         }
         adb.setNegativeButton(R.string.no2) { p0, p1 -> }
         adb.create().show()
     }*/

    private fun basicCard(detections: String): String? {
        val regex = "\\d{4}\\D\\d{4}\\D\\d{4}\\D\\d{4}".toRegex()
        val matcher = regex.find(detections)
        return if (matcher != null) {
            val cardNumber = detections.substring(matcher.range.first, matcher.range.last + 1)
            if (cardNumber.startsWith("8600") || cardNumber.startsWith("9860") || cardNumber.startsWith(
                    "6262"
                )
            ) cardNumber else null
        } else null
    }

    private fun uzCard(detections: String): String? {
        val regex = "^8[0-9]{12}(?:[0-9]{3})?".toRegex()
        val matcher = regex.find(detections)
        return if (matcher != null) {
            val cardNumber = detections.substring(matcher.range.first, matcher.range.last + 1)
            if (cardNumber.startsWith("8600")) cardNumber else null
        } else null
    }

    private fun humoCard(detections: String): String? {
        val regex = "^9[0-9]{12}(?:[0-9]{3})?".toRegex()
        val matcher = regex.find(detections)
        return if (matcher != null) {
            val cardNumber = detections.substring(matcher.range.first, matcher.range.last + 1)
            if (cardNumber.startsWith("9860")) cardNumber else null
        } else null
    }

    private fun unionCard(detections: String): String? {
        val regex = "^6[0-9]{12}(?:[0-9]{3})?".toRegex()
        val matcher = regex.find(detections)
        return if (matcher != null) {
            val cardNumber = detections.substring(matcher.range.first, matcher.range.last + 1)
            if (cardNumber.startsWith("6262")) cardNumber else null
        } else null
    }

    private fun attoCard(detections: String): String? {
        val regex = "^9[0-9]{12}(?:[0-9]{3})?".toRegex()
        val matcher = regex.find(detections)
        return if (matcher != null) {
            val cardNumber = detections.substring(matcher.range.first, matcher.range.last + 1)
            if (cardNumber.startsWith("9987")) cardNumber else null
        } else null
    }

    private fun expireDate(detections: String): String? {
//        val regex = "^(0[1-9]|1[0-2])([ ]?)\\/([ ]?)([0-9]{2})\$".toRegex()
        val regex = "\\d{2}\\/\\d{2}".toRegex()
        val matcher = regex.find(detections)
        return if (matcher != null) {
            val cardExpire = detections.substring(matcher.range.first, matcher.range.last + 1)
            cardExpire
        } else null

    }
}
