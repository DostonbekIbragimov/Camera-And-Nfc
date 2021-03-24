package uz.ssd.sdk.nfc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;

public class NFCActivity extends AppCompatActivity implements CardNfcAsyncTask.CardNfcInterface {

    private CardNfcAsyncTask mCardNfcAsyncTask;
    private Toolbar mToolbar;
    private LinearLayout mCardReadyContent;
    //    private TextView mPutCardContent;
    private TextView mCardNumberText;
    private TextView mExpireDateText;
    private ImageView mCardLogoIcon;
    private NfcAdapter mNfcAdapter;
    private AlertDialog mTurnNfcDialog;
    private ProgressDialog mProgressDialog;
    private Button buttonOk;
    private String mDoNotMoveCardMessage;
    private String mUnknownEmvCardMessage;
    private String mCardWithLockedNfcMessage;
    private boolean mIsScanNow;
    private boolean mIntentFromCreate;
    private CardNfcUtils mCardNfcUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_f_c);
        mToolbar = findViewById(R.id.toolbarA);
        setSupportActionBar(mToolbar);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
//            TextView noNfc = findViewById(R.id.candidatesArea);
//            noNfc.setVisibility(View.VISIBLE);
        } else {
            mCardNfcUtils = new CardNfcUtils(this);
//            mPutCardContent = findViewById(R.id.content_putCard);
            mCardReadyContent = findViewById(R.id.content_cardReady);
            mCardNumberText = findViewById(R.id.text1);
            mExpireDateText = findViewById(R.id.text2);
            mCardLogoIcon = findViewById(R.id.icon);
            createProgressDialog();
            initNfcMessages();
            mIntentFromCreate = true;
            onNewIntent(getIntent());
        }
        buttonOk = findViewById(R.id.ready_card);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                if (mExpireDateText != null && mCardNumberText != null) {
                    returnIntent.putExtra("card_number", convert(mCardNumberText.getText().toString()));
                    returnIntent.putExtra("card_expire", mExpireDateText.getText().toString());
                    setResult(Activity.RESULT_OK, returnIntent);
                } else {
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                }
                finish();
            }
        });

//        returnIntent = Intent()
//        returnIntent.putExtra("card_number", cardNumber)
//        returnIntent.putExtra("card_expire", cardExpire)
//        setResult(Activity.RESULT_OK, returnIntent)
//        finish()
    }

    private String convert(String cardNumber) {
        cardNumber.replaceAll(" ", "");
        cardNumber.replaceAll("-", "");
        return cardNumber;
    }

    private void closePage() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIntentFromCreate = false;
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
            showTurnOnNfcDialog();
//            mPutCardContent.setVisibility(View.GONE);
        } else if (mNfcAdapter != null) {
            if (!mIsScanNow) {
//                mPutCardContent.setVisibility(View.VISIBLE);
                mCardReadyContent.setVisibility(View.GONE);
            }
            mCardNfcUtils.enableDispatch();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mCardNfcUtils.disableDispatch();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate)
                    .build();
        }
    }

    @Override
    public void startNfcReadCard() {
        mIsScanNow = true;
        mProgressDialog.show();
    }

    @Override
    public void cardIsReadyToRead() {
//        mPutCardContent.setVisibility(View.GONE);
        mCardReadyContent.setVisibility(View.VISIBLE);
        String card = mCardNfcAsyncTask.getCardNumber();
        card = getPrettyCardNumber(card);
        String expiredDate = mCardNfcAsyncTask.getCardExpireDate();
        String cardType = mCardNfcAsyncTask.getCardType();
        mCardNumberText.setText(card);
        mExpireDateText.setText(expiredDate);
        parseCardType(cardType);
    }

    @Override
    public void doNotMoveCardSoFast() {
        showSnackBar(mDoNotMoveCardMessage);
    }

    @Override
    public void unknownEmvCard() {
        showSnackBar(mUnknownEmvCardMessage);
    }

    @Override
    public void cardWithLockedNfc() {
        showSnackBar(mCardWithLockedNfcMessage);
    }

    @Override
    public void finishNfcReadCard() {
        mProgressDialog.dismiss();
//        mCardNfcAsyncTask = null;
        mIsScanNow = false;
    }

    private void createProgressDialog() {
        String title = getString(R.string.ad_progressBar_title);
        String mess = getString(R.string.ad_progressBar_mess);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(mess);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
    }

    private void showSnackBar(String message) {
        Snackbar.make(mToolbar, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showTurnOnNfcDialog() {
        if (mTurnNfcDialog == null) {
            String title = getString(R.string.ad_nfcTurnOn_title);
            String mess = getString(R.string.ad_nfcTurnOn_message);
            String pos = getString(R.string.ad_nfcTurnOn_pos);
            String neg = getString(R.string.ad_nfcTurnOn_neg);
            mTurnNfcDialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(mess)
                    .setPositiveButton(pos, (dialogInterface, i) -> {
                        // Send the user to the settings page and hope they turn it on
                        startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                    })
                    .setNegativeButton(neg, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    }).create();
        }
        mTurnNfcDialog.show();
    }

    private void initNfcMessages() {
        mDoNotMoveCardMessage = getString(R.string.snack_doNotMoveCard);
        mCardWithLockedNfcMessage = getString(R.string.snack_lockedNfcCard);
        mUnknownEmvCardMessage = getString(R.string.snack_unknownEmv);
    }


    private void parseCardType(String cardType) {
        if (cardType.equals(CardNfcAsyncTask.CARD_UNKNOWN)) {
            Snackbar.make(mToolbar, getString(R.string.snack_unknown_bank_card), Snackbar.LENGTH_LONG)
                    .setAction("GO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goToRepo();
                        }
                    });
        } else if (cardType.equals(CardNfcAsyncTask.CARD_VISA)) {
            mCardLogoIcon.setImageResource(R.drawable.ic_grid);
        } else if (cardType.equals(CardNfcAsyncTask.CARD_MASTER_CARD)) {
            mCardLogoIcon.setImageResource(R.drawable.ic_uzcard);
        }
    }

    private String getPrettyCardNumber(String card) {
        String div = " - ";
        return card.substring(0, 4) + div + card.substring(4, 8) + div + card.substring(8, 12)
                + div + card.substring(12, 16);
    }

    private void goToRepo() {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.repoUrl)));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setPackage("com.android.chrome");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            i.setPackage(null);
            startActivity(i);
        }
    }
}