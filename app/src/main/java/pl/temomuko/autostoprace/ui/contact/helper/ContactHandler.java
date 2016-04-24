package pl.temomuko.autostoprace.ui.contact.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.DrawableRes;

import java.util.UnknownFormatFlagsException;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.injection.ActivityContext;
import pl.temomuko.autostoprace.util.IntentUtil;

/**
 * Created by Rafał Naniewicz on 18.04.2016.
 */
public class ContactHandler {

    private static final String PHONE_NUMBER = "phone_number";
    private static final String SMS = "sms";
    private static final String EMAIL = "email";
    private static final String WEB_PAGE = "web_page";
    private static final String FAN_PAGE = "fan_page";

    private Context mContext;

    @Inject
    public ContactHandler(@ActivityContext Context context) {
        mContext = context;
    }

    public static
    @DrawableRes
    int getIcon(String contactRowType) throws UnknownFormatFlagsException {
        switch (contactRowType) {
            case PHONE_NUMBER:
                return R.drawable.ic_call_black_24dp;
            case SMS:
                return R.drawable.ic_textsms_black_24dp;
            case EMAIL:
                return R.drawable.ic_email_black_24dp;
            case WEB_PAGE:
                return R.drawable.ic_public_black_24dp;
            case FAN_PAGE:
                return R.drawable.ic_fb_logo_24dp;
            default:
                throw new UnknownFormatFlagsException(contactRowType);
        }
    }

    public static boolean canSetColorFilter(String contactRowType) {
        return !contactRowType.equals(FAN_PAGE);
    }

    public void startIntent(String contactRowType, String value) throws NoIntentHandlerException {
        switch (contactRowType) {
            case PHONE_NUMBER:
                startPhoneIntent(value);
                break;
            case SMS:
                startSmsIntent(value);
                break;
            case EMAIL:
                startEmailIntent(value);
                break;
            case WEB_PAGE:
                startWebPageIntent(value);
                break;
            case FAN_PAGE:
                startFanPage(value);
                break;
        }
    }

    private void startPhoneIntent(String phoneNumber) throws NoIntentHandlerException {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:".concat(phoneNumber)));
        IntentUtil.addClearBackStackIntentFlags(dialIntent);
        if (dialIntent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(dialIntent);
        } else {
            throw new NoIntentHandlerException();
        }
    }

    private void startSmsIntent(String smsNumber) throws NoIntentHandlerException {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("sms:".concat(smsNumber)));
        IntentUtil.addClearBackStackIntentFlags(smsIntent);
        if (smsIntent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(smsIntent);
        } else {
            throw new NoIntentHandlerException();
        }
    }

    private void startEmailIntent(String email) throws NoIntentHandlerException {
        Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO);
        sendEmailIntent.setData(Uri.parse("mailto: ".concat(email)));
        IntentUtil.addClearBackStackIntentFlags(sendEmailIntent);
        if (sendEmailIntent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(sendEmailIntent);
        } else {
            throw new NoIntentHandlerException();
        }
    }

    private void startWebPageIntent(String webPage) throws NoIntentHandlerException {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webPage));
        IntentUtil.addClearBackStackIntentFlags(intent);
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        } else {
            throw new NoIntentHandlerException();
        }
    }

    private void startFanPage(String fanPageId) throws NoIntentHandlerException {
        Intent fanPageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/".concat(fanPageId)));
        IntentUtil.addClearBackStackIntentFlags(fanPageIntent);
        if (fanPageIntent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(fanPageIntent);
        } else {
            String fanPageAddress = "http://www.facebook.com/".concat(fanPageId);
            startWebPageIntent(fanPageAddress);
        }
    }
}
