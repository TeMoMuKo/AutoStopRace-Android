package pl.temomuko.autostoprace.util;

import android.content.Context;
import android.util.Patterns;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by szymen on 2016-02-04.
 */

@Singleton
public class LoginValidator {

    private Context mContext;

    @Inject
    public LoginValidator(@AppContext Context context) {
        mContext = context;
    }

    public boolean isEmailValid(String email) {
        return getEmailValidErrorMessage(email).isEmpty();
    }

    public boolean isPasswordValid(String password) {
        return getPasswordValidErrorMessage(password).isEmpty();
    }

    public String getEmailValidErrorMessage(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() ?
                "" : mContext.getString(R.string.error_invalid_email);
    }

    public String getPasswordValidErrorMessage(String password) {
        return !password.isEmpty() ? "" : mContext.getString(R.string.error_empty_pass);
    }
}
