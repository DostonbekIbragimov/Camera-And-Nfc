/* NFC Reader is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

NFC Reader is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Wget.  If not, see <http://www.gnu.org/licenses/>.

Additional permission under GNU GPL version 3 section 7 */

package uz.ssd.sdk.nfc.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import uz.ssd.sdk.nfc.R;
import uz.ssd.sdk.nfc.SPEC;
import uz.ssd.sdk.nfc.bean.Card;
import uz.ssd.sdk.nfc.reader.ReaderListener;

public final class NfcPage implements ReaderListener {
    private static final String TAG = "READCARD_ACTION";
    private static final String RET = "READCARD_RESULT";
    private static final String STA = "READCARD_STATUS";
    private Context context;
    private final Activity activity;

    public NfcPage(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public static boolean isSendByMe(Intent intent) {
        return intent != null && TAG.equals(intent.getAction());
    }

    public static boolean isNormalInfo(Intent intent) {
        return intent != null && intent.hasExtra(STA);
    }

    public static CharSequence getContent(Activity activity, Intent intent) {

        String info = intent.getStringExtra(RET);
        if (info == null || info.length() == 0)
            return null;

        return new SpanFormatter(AboutPage.getActionHandler(activity))
                .toSpanned(info);
    }

    @Override
    public void onReadEvent(SPEC.EVENT event, Object... objs) {
        if (event == SPEC.EVENT.IDLE) {
            showProgressBar();
        } else if (event == SPEC.EVENT.FINISHED) {
            hideProgressBar();

            final Card card;
            if (objs != null && objs.length > 0)
                card = (Card) objs[0];
            else
                card = null;

            activity.setIntent(buildResult(card, context));
        }
    }

    private Intent buildResult(Card card, Context context) {
        final Intent ret = new Intent(TAG);
        this.context = context;
        if (context != null)
            if (card != null && !card.hasReadingException()) {
                if (card.isUnknownCard()) {
                    ret.putExtra(RET, context.getResources().getString(R.string.snack_unknown_bank_card));
                } else {
                    ret.putExtra(RET, card.toHtml());
                    ret.putExtra(STA, 1);
                }
            } else {
                ret.putExtra(RET, context.getResources().getString(R.string.spec_app_unknown));
            }
        return ret;
    }

    private void showProgressBar() {

    }

    private void hideProgressBar() {

    }
}
