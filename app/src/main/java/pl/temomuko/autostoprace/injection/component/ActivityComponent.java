package pl.temomuko.autostoprace.injection.component;

import dagger.Component;
import pl.temomuko.autostoprace.injection.ActivityScope;
import pl.temomuko.autostoprace.injection.module.ActivityModule;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.login.LoginActivity;
import pl.temomuko.autostoprace.ui.login.resetpass.ResetPassActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;
import pl.temomuko.autostoprace.ui.phrasebook.PhrasebookActivity;
import pl.temomuko.autostoprace.ui.post.PostActivity;
import pl.temomuko.autostoprace.ui.settings.SettingsFragment;
import pl.temomuko.autostoprace.ui.staticdata.about.AboutActivity;
import pl.temomuko.autostoprace.ui.staticdata.campus.CampusActivity;
import pl.temomuko.autostoprace.ui.staticdata.contact.ContactActivity;
import pl.temomuko.autostoprace.ui.staticdata.launcher.LauncherActivity;
import pl.temomuko.autostoprace.ui.teamslocations.TeamsLocationsActivity;

/**
 * Created by Szymon Kozak on 2016-01-06.
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(BaseActivity baseActivity);

    void inject(MainActivity mainActivity);

    void inject(LoginActivity loginActivity);

    void inject(ResetPassActivity resetPassActivity);

    void inject(LauncherActivity launcherActivity);

    void inject(PostActivity postActivity);

    void inject(TeamsLocationsActivity teamsLocationsActivity);

    void inject(CampusActivity campusActivity);

    void inject(PhrasebookActivity phrasebookActivity);

    void inject(ContactActivity contactActivity);

    void inject(SettingsFragment settingsFragment);

    void inject(AboutActivity aboutActivity);
}
