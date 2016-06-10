package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.injection.AppContext;

/**
 * Created by Rafał Naniewicz on 02.04.2016.
 */
public class TeamLocationInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    @BindView(R.id.tv_location_record_date) TextView mLocationRecordDateTextView;
    @BindView(R.id.tv_location_record_message) TextView mLocationRecordMessageTextView;

    private final View mContentsView;

    @Inject
    public TeamLocationInfoWindowAdapter(@AppContext Context context) {
        mContentsView = LayoutInflater.from(context).inflate(R.layout.adapter_team_location_info_window, null, false);
        ButterKnife.bind(this, mContentsView);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        mLocationRecordMessageTextView.setText(marker.getTitle());
        mLocationRecordDateTextView.setText(marker.getSnippet());
        return mContentsView;
    }
}
