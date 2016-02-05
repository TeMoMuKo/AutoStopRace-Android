package pl.temomuko.autostoprace.injection.component;

import dagger.Component;
import pl.temomuko.autostoprace.injection.ActivityScope;
import pl.temomuko.autostoprace.injection.module.ActivityModule;
import pl.temomuko.autostoprace.ui.about.AboutActivity;
import pl.temomuko.autostoprace.ui.base.BaseActivity;
import pl.temomuko.autostoprace.ui.campus.CampusActivity;
import pl.temomuko.autostoprace.ui.contact.ContactActivity;
import pl.temomuko.autostoprace.ui.launcher.LauncherActivity;
import pl.temomuko.autostoprace.ui.login.LoginActivity;
import pl.temomuko.autostoprace.ui.main.MainActivity;
import pl.temomuko.autostoprace.ui.phrasebook.PhrasebookActivity;
import pl.temomuko.autostoprace.ui.post.PostActivity;
import pl.temomuko.autostoprace.ui.schedule.ScheduleActivity;
import pl.temomuko.autostoprace.ui.settings.SettingsActivity;
import pl.temomuko.autostoprace.ui.teams.TeamsActivity;

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

    void inject(PostActivity postActivity);

    void inject(TeamsActivity teamsActivity);

    void inject(ScheduleActivity scheduleActivity);

    void inject(CampusActivity campusActivity);

    void inject(PhrasebookActivity phrasebookActivity);

    void inject(ContactActivity contactActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(AboutActivity aboutActivity);
}
