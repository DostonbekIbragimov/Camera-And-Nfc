package uz.ssd.sdk.nfc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import uz.ssd.sdk.nfc.ui.MainPage
import uz.ssd.sdk.nfc.ui.NfcPage


class NfcActivity : AppCompatActivity() {
    private var mNfcManager: NfcManager? = null
    private val mTextArea: TextView
        get() = findViewById(R.id.text1)
    private var mSafeExit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_aar)
        setSupportActionBar(findViewById(R.id.toolbar))
        mNfcManager = NfcManager(this)

        mTextArea.movementMethod = LinkMovementMethod.getInstance()
        onNewIntent(intent)

        mTextArea.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val cardNumber = getCard(s.toString())
                val cardExpire = getExpiry(s.toString())
                if (cardNumber != null && cardExpire != null) {
                    val returnIntent = Intent()
                    Toast.makeText(this@NfcActivity, "CardNumber: $cardNumber \nCardExpire:  $cardExpire", Toast.LENGTH_SHORT).show()
                    returnIntent.putExtra("card_number", getPrettyCard(cardNumber))
                    returnIntent.putExtra("card_expire", cardExpire)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            }
        })
    }

    private fun getCard(detections: String): String? {
        val regex = "\\d{16}".toRegex()
        val matcher = regex.find(detections)
        return if (matcher != null) {
            val cardNumber = detections.substring(matcher.range.first, matcher.range.last + 1)
            cardNumber
        } else null
    }

    private fun getPrettyCard(card: String): String {
        val div = " "
        return (card.substring(0, 4) + div + card.substring(4, 8) + div + card.substring(8, 12)
                + div + card.substring(12, 16))
    }

    private fun getExpiry(detections: String): String? {
        val regex = "[-] \\d{4}[.]\\d{2}[.]\\d{2}".toRegex()
        val matcher = regex.find(detections)
        return if (matcher != null) {
            val card = detections.substring(matcher.range.first, matcher.range.last + 1)
            if (card.length > 9) {
                val year = card.substring(2, 6)
                val month = card.substring(7, 9)
                val cardExpiry = "$month/${year.substring(2, 4)}"
                cardExpiry
            } else null
        } else null
    }

    override fun onBackPressed() {
        if (mSafeExit) {
            super.onBackPressed()
        }
    }

    override fun setIntent(intent: Intent) {
        if (NfcPage.isSendByMe(intent)) {
            loadNfcPage(intent, mTextArea)
        } else {
            super.setIntent(intent)
        }
    }

    override fun onPause() {
        mNfcManager?.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mNfcManager?.onResume()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            if (mNfcManager!!.updateStatus()) {
                loadDefaultPage(mTextArea)
            }
            Handler().postDelayed({ mSafeExit = true }, 800)
        } else {
            mSafeExit = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadDefaultPage(mTextArea)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (!mNfcManager!!.readCard(intent, NfcPage(this, this))) {
            loadDefaultPage(mTextArea)
        }
    }

    private fun loadDefaultPage(textArea: TextView?) {
        resetTextArea(textArea, SPEC.PAGE.DEFAULT, Gravity.CENTER)
        textArea!!.text = MainPage.getContent(this, this)
        val padding = resources.getDimensionPixelSize(R.dimen.padding_default)
        textArea.setPadding(padding, padding, padding, padding)
    }

    @SuppressLint("RtlHardcoded")
    private fun loadNfcPage(intent: Intent, textArea: TextView?) {
        val info: CharSequence = NfcPage.getContent(this, intent)
        if (NfcPage.isNormalInfo(intent)) {
            resetTextArea(textArea, SPEC.PAGE.INFO, Gravity.LEFT)
            val padding = resources.getDimensionPixelSize(R.dimen.padding_window)
            textArea!!.setPadding(padding, padding, padding, padding)
        } else {
            resetTextArea(textArea, SPEC.PAGE.ABOUT, Gravity.CENTER)
            val padding = resources.getDimensionPixelSize(R.dimen.padding_default)
            textArea!!.setPadding(padding, padding, padding, padding)
        }
        textArea.text = info
    }

    private fun resetTextArea(textArea: TextView?, type: SPEC.PAGE, gravity: Int) {
        (textArea!!.parent as View).scrollTo(0, 0)
        textArea.tag = type
        textArea.gravity = gravity
    }
}