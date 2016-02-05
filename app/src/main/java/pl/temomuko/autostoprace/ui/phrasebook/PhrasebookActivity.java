package pl.temomuko.autostoprace.ui.phrasebook;

import android.os.Bundle;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;

/**
 * Created by szymen on 2016-02-05.
 */
public class PhrasebookActivity extends DrawerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrasebook);
        setupToolbarWithBack();
    }
}
