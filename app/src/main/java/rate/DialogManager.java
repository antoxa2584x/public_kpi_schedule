package rate;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import com.afollestad.materialdialogs.MaterialDialog;
import com.goldenpiedevs.schedule.app.R;

final class DialogManager {
    private static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";

    private DialogManager() {
    }

    static Dialog create(final Context context) {

        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context)
                .title(R.string.rate_dialog_title)
                .content(context.getString(R.string.rate_dialog_message))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        String packageName = context.getPackageName();
                        Intent intent = new Intent(Intent.ACTION_VIEW, UriHelper.getGooglePlay(packageName));
                        if (UriHelper.isPackageExists(context, GOOGLE_PLAY_PACKAGE_NAME)) {
                            intent.setPackage(GOOGLE_PLAY_PACKAGE_NAME);
                        }
                        context.startActivity(intent);
                        PreferenceHelper.setAgreeShowDialog(context, false);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        PreferenceHelper.setRemindInterval(context);
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        super.onNeutral(dialog);
                        PreferenceHelper.setAgreeShowDialog(context, false);
                    }
                })
                .positiveText(R.string.rate_dialog_ok)
                .negativeText(R.string.rate_dialog_no)
                .neutralText(R.string.rate_dialog_cancel);

        return dialog.build();
    }

}