package pl.temomuko.autostoprace.util;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.login.LoginPresenter;
import pl.temomuko.autostoprace.ui.settings.SettingsPresenter;

/**
 * Created by szymen on 2016-02-06.
 */
public class DialogFactory {

    public static MaterialDialog createLoggingProcessDialog(Context context,
                                                            LoginPresenter loginPresenter) {
        return new MaterialDialog.Builder(context)
                .title(R.string.title_logging)
                .content(R.string.please_wait)
                .cancelListener(dialog -> loginPresenter.cancelSignInRequest())
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
}
