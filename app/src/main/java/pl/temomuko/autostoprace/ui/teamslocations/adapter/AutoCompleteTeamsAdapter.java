package pl.temomuko.autostoprace.ui.teamslocations.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;

/**
 * Created by Rafał Naniewicz on 09.04.2016.
 */
public class AutoCompleteTeamsAdapter extends ArrayAdapter<Team> {

    private static final String TAG = AutoCompleteTeamsAdapter.class.getSimpleName();

    private List<Team> mOriginalTeamList;
    private OnTeamHintSelectedListener mTeamHintSelectedListener;

    public interface OnTeamHintSelectedListener {

        void onTeamHintClick(int teamId);
    }

    public AutoCompleteTeamsAdapter(Context context) {
        super(context, R.layout.item_autocomplete_team, new ArrayList<>());
        mOriginalTeamList = new ArrayList<>();
    }

    public void setTeamSelectedListener(OnTeamHintSelectedListener teamHintSelectedListener) {
        mTeamHintSelectedListener = teamHintSelectedListener;
    }

    public void setOriginalTeamList(List<Team> originalTeamList) {
        mOriginalTeamList = originalTeamList;
    }

    public void replaceFilteredTeamList(List<Team> filteredTeamList) {
        clear();
        addAll(filteredTeamList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new TeamsFilter(this, mOriginalTeamList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Team currentTeam = getItem(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
            holder.position = position;
        } else {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_autocomplete_team, parent, false);
            holder = new ViewHolder(convertView);
            holder.position = position;
            convertView.setTag(holder);
            convertView.setOnClickListener(view -> {
                if (mTeamHintSelectedListener != null) {
                    mTeamHintSelectedListener.onTeamHintClick(getItem(holder.position).getId());
                }
            });
        }
        setTeamRowContent(holder, currentTeam);
        return convertView;
    }

    private void setTeamRowContent(ViewHolder holder, Team viewTeam) {
        holder.mTeamIdTextView.setText(String.valueOf(viewTeam.getId()));
        LocationRecord lastLocationRecord = viewTeam.getLastLocationRecord();
        if (lastLocationRecord == null) {
            holder.mLastLocationTextView.setVisibility(View.GONE);
        } else {
            holder.mLastLocationTextView.setVisibility(View.VISIBLE);
            holder.mLastLocationTextView.setText(getContext().getString(R.string.msg_last_location_record_received_adapter,
                    viewTeam.getLastLocationRecord().getAddress()));
        }
    }

    static class ViewHolder {

        @Bind(R.id.tv_team_id) TextView mTeamIdTextView;
        @Bind(R.id.tv_last_location) TextView mLastLocationTextView;
        int position;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
