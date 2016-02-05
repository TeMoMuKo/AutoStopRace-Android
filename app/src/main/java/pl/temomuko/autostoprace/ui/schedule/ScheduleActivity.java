package pl.temomuko.autostoprace.ui.schedule;

import android.os.Bundle;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;

/**
 * Created by szymen on 2016-02-04.
 */
public class ScheduleActivity extends DrawerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        setupToolbarWithToggle();
    }
}
