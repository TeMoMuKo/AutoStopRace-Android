package pl.temomuko.autostoprace.injection.component;

import dagger.Component;
import pl.temomuko.autostoprace.injection.ActivityScope;
import pl.temomuko.autostoprace.injection.module.ActivityModule;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.launcher.LauncherActivity;
import pl.temomuko.autostoprace.ui.login.LoginActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;

/**
 * Created by szymen on 2016-01-06.
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(BaseActivity baseActivity);

    void inject(MainActivity mainActivity);

    void inject(LoginActivity loginActivity);

    void inject(LauncherActivity launcherActivity);
}
