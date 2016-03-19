package pl.temomuko.autostoprace.util;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.login.LoginPresenter;
import pl.temomuko.autostoprace.ui.login.resetpass.ResetPassPresenter;
import pl.temomuko.autostoprace.ui.settings.SettingsPresenter;

/**
 * Created by Szymon Kozak on 2016-02-06.
 */
public final class DialogFactory {

    private DialogFactory() {
        throw new AssertionError();
    }

    public static MaterialDialog createLoggingProcessDialog(Context context, LoginPresenter presenter) {
        return new MaterialDialog.Builder(context)
                .title(R.string.title_logging)
                .content(R.string.please_wait)
                .cancelListener(dialog -> presenter.cancelSignInRequest())
                .progress(true, 0)
                .build();
    }

    public static MaterialDialog createResetPassProcessDialog(Context context, ResetPassPresenter presenter) {
        return new MaterialDialog.Builder(context)
                .title(R.string.title_reset_pass)
                .content(R.string.please_wait)
                .cancelListener(dialog -> presenter.cancelResetPassRequest())
                .progress(true, 0)
                .build();
    }

    public static MaterialDialog createLogoutInfoDialog(Context context,
                                                        SettingsPresenter settingsPresenter) {
        return new MaterialDialog.Builder(context)
                .title(R.string.msg_logout_question)
                .content(R.string.msg_logout_info)
                .positiveText(R.string.msg_logout)
                .negativeText(R.string.msg_cancel)
                .onPositive((dialog, which) -> settingsPresenter.logout())
                .build();
    }

    public static class HelpDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new MaterialDialog.Builder(getActivity())
                    .title(R.string.help)
                    .content(R.string.msg_login_info)
                    .positiveText(R.string.ok)
                    .build();
        }

        public static HelpDialogFragment create() {
            return new HelpDialogFragment();
        }
    }
}
