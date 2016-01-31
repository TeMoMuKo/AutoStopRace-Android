package pl.temomuko.autostoprace.injection.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.temomuko.autostoprace.data.remote.AsrService;
import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by szymen on 2016-01-06.
 */

@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @AppContext
    Context providesContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    AsrService provideAsrService() {
        return AsrService.Factory.createAsrService();
    }
}
