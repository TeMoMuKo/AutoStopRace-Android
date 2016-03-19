package pl.temomuko.autostoprace.data.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pl.temomuko.autostoprace.data.remote.AsrService;
import pl.temomuko.autostoprace.util.LogUtil;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by Szymon Kozak on 2016-01-22.
 */
public class ApiError {

    private int mStatus;
    private List<String> mErrors = new ArrayList<>();
    private final static String EMAIL_CONFIRMATION_ERROR_SUFFIX =
            "You must follow the instructions in the email before your account can be activated";
    private final static String RESET_PASS_ERROR_PREFIX =
            "Unable to find user with email";
    private final static String TAG = ApiError.class.getSimpleName();

    private ApiError(int status) {
        mStatus = status;
    }

    public ApiError(Response response) {
        this(response.code());
        setupSignInResponseErrors(response);
        setupResetPassResponseErrors(response);
    }

    private void setupResetPassResponseErrors(Response response) {
        try {
            Converter<ResponseBody, ResetPassResponse> converter
                    = AsrService.Factory.getResetPassErrorResponseConverter();
            if (converter != null) {
                ResetPassResponse resetPassResponse = converter.convert(response.errorBody());
                mErrors = resetPassResponse.getErrors();
            }
        } catch (IOException e) {
            LogUtil.i(TAG, "It isn't ResetPassResponse error.");
        }
    }

    private void setupSignInResponseErrors(Response response) {
        try {
            Converter<ResponseBody, SignInResponse> converter
                    = AsrService.Factory.getSignInErrorResponseConverter();
            if (converter != null) {
                SignInResponse signInResponse = converter.convert(response.errorBody());
                mErrors = signInResponse.getErrors();
            }
        } catch (IOException e) {
            LogUtil.i(TAG, "It isn't SignInResponse error.");
        }
    }

    public boolean isEmailConfirmationError() {
        for (String errorMessage : mErrors) {
            if (errorMessage.endsWith(EMAIL_CONFIRMATION_ERROR_SUFFIX)) return true;
        }
        return false;
    }

    public boolean isResetPassError() {
        for (String errorMessage : mErrors) {
            if (errorMessage.startsWith(RESET_PASS_ERROR_PREFIX)) return true;
        }
        return false;
    }

    public int getStatus() {
        return mStatus;
    }
}
