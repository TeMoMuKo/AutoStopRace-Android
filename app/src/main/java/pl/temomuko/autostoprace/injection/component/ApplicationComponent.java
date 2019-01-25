package pl.temomuko.autostoprace.injection.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import pl.temomuko.autostoprace.AsrApplication;
import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.local.PrefsHelper;
import pl.temomuko.autostoprace.data.local.photo.ImageController;
import pl.temomuko.autostoprace.data.remote.ErrorHandler;
import pl.temomuko.autostoprace.data.remote.api.Asr2019Service;
import pl.temomuko.autostoprace.injection.AppContext;
import pl.temomuko.autostoprace.injection.module.ApplicationModule;
import pl.temomuko.autostoprace.injection.module.NetworkModule;
import pl.temomuko.autostoprace.service.LocationSyncService;
import pl.temomuko.autostoprace.ui.main.Shortcuts;

/**
 * Created by Szymon Kozak on 2016-01-06.
 */
@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {

    void inject(LocationSyncService locationSyncService);

    @AppContext
    Context context();

    Application application();

    Asr2019Service asr2019Service();

    DataManager dataManager();

    ErrorHandler errorHandler();

    PrefsHelper prefsHelper();

    ImageController imageController();

    Shortcuts shortcuts();

    void inject(AsrApplication asrApplication);
}