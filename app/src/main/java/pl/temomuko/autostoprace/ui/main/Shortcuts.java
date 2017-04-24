package pl.temomuko.autostoprace.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by Szymon Kozak on 22.04.2017.
 */

@Singleton
public final class Shortcuts {

    public static final String ACTION_POST_LOCATION = "pl.temomuko.autostoprace.action.post";
    public static final String ACTION_LOCATIONS_MAP = "pl.temomuko.autostoprace.action.locationsmap";
    public static final String ACTION_PHRASEBOOK = "pl.temomuko.autostoprace.action.phrasebook";

    public static final String POST_LOCATION = "shortcut_post_location";
    public static final String LOCATIONS_MAP = "shortcut_locations_map";
    public static final String PHRASEBOOK = "shortcut_phrasebook";

    private Context mAppContext;

    @Inject
    public Shortcuts(@AppContext Context appContext) {
        this.mAppContext = appContext;
    }

    public void setPostLocationShortcutEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = mAppContext.getSystemService(ShortcutManager.class);
            if (enabled) {
                if (shortcutManager.getDynamicShortcuts().size() == 0) {
                    createPostLocationsShortcut();
                }
                shortcutManager.enableShortcuts(Collections.singletonList(Shortcuts.POST_LOCATION));
            } else {
                shortcutManager.disableShortcuts(Collections.singletonList(Shortcuts.POST_LOCATION));
            }
        }
    }

    private void createPostLocationsShortcut() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            Intent intent = new Intent(ACTION_POST_LOCATION);
            intent.setPackage(mAppContext.getPackageName());
            intent.setClass(mAppContext, MainActivity.class);
            ShortcutManager shortcutManager = mAppContext.getSystemService(ShortcutManager.class);
            ShortcutInfo shortcut = new ShortcutInfo.Builder(mAppContext, Shortcuts.POST_LOCATION)
                    .setShortLabel(mAppContext.getString(R.string.shortcut_post_short_label))
                    .setDisabledMessage(mAppContext.getString(R.string.shortcut_post_disabled_label))
                    .setLongLabel(mAppContext.getString(R.string.shortcut_post_long_label))
                    .setIcon(Icon.createWithResource(mAppContext, R.drawable.ic_shortcut_add_location))
                    .setIntent(intent)
                    .build();
            shortcutManager.setDynamicShortcuts(Collections.singletonList(shortcut));
        }
    }
}
