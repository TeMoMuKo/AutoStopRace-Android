package pl.temomuko.autostoprace.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;

import java.util.Arrays;
import java.util.Collections;

import javax.inject.Inject;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by Szymon Kozak on 22.04.2017.
 */

public final class Shortcuts {

    public static final String ACTION_POST_LOCATION = "pl.temomuko.autostoprace.action.post";
    public static final String ACTION_LOCATIONS_MAP = "pl.temomuko.autostoprace.action.locationsmap";
    public static final String ACTION_PHRASEBOOK = "pl.temomuko.autostoprace.action.phrasebook";

    public static final String POST_LOCATION = "shortcut_post_location";
    public static final String LOCATIONS_MAP = "shortcut_locations_map";
    public static final String PHRASEBOOK = "shortcut_phrasebook";

    private Context appContext;

    @Inject
    public Shortcuts(@AppContext Context appContext) {
        this.appContext = appContext;
    }

    public void createPostLocationsShortcut() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            Intent intent = new Intent(ACTION_POST_LOCATION);
            intent.setPackage(appContext.getPackageName());
            intent.setClass(appContext, MainActivity.class);
            ShortcutManager shortcutManager = appContext.getSystemService(ShortcutManager.class);
            ShortcutInfo shortcut = new ShortcutInfo.Builder(appContext, Shortcuts.POST_LOCATION)
                    .setShortLabel(appContext.getString(R.string.shortcut_post_short_label))
                    .setDisabledMessage(appContext.getString(R.string.shortcut_post_disabled_label))
                    .setLongLabel(appContext.getString(R.string.shortcut_post_long_label))
                    .setIcon(Icon.createWithResource(appContext, R.drawable.ic_shortcut_add_location))
                    .setIntent(intent)
                    .build();
            shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
        }
    }

    public void setPostLocationShortcutEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            if (enabled) {
                appContext.getSystemService(ShortcutManager.class).enableShortcuts(Collections.singletonList(Shortcuts.POST_LOCATION));
            } else {
                appContext.getSystemService(ShortcutManager.class).disableShortcuts(Collections.singletonList(Shortcuts.POST_LOCATION));
            }
        }
    }
}
