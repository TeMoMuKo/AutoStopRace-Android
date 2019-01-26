package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.searchteamview;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.util.LocationInfoProvider;

/**
 * Created by Rafał Naniewicz on 23.04.2016.
 */
public class SearchTeamHintsAdapter extends RecyclerView.Adapter<SearchTeamHintsAdapter.ViewHolder>
        implements Filterable {

    private static final String BUNDLE_ALL_TEAM_LIST = "bundle_all_team_list";
    private static final String BUNDLE_ACTUAL_TEAM_LIST = "bundle_actual_team_list";

    private final Context mContext;
    private final Filter mFilter;
    private final OnTeamHintSelectedListener mOnTeamHintSelectedListener;
    private final TeamFilterResultsListener mTeamFilterResultsListener;
    private List<Team> mAllTeams;
    private List<Team> mActualTeams;
    private LocationInfoProvider locationInfoProvider = new LocationInfoProvider();

    public SearchTeamHintsAdapter(Context context, OnTeamHintSelectedListener onTeamHintSelectedListener,
                                  TeamFilterResultsListener teamFilterResultsListener) {
        mContext = context;
        mAllTeams = new ArrayList<>();
        mActualTeams = new ArrayList<>();
        mFilter = new SearchTeamViewFilter();
        mOnTeamHintSelectedListener = onTeamHintSelectedListener;
        mTeamFilterResultsListener = teamFilterResultsListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_autocomplete_team, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Team currentTeam = mActualTeams.get(position);
        holder.mTeamIdTextView.setText(String.valueOf(currentTeam.getTeamNumber()));
        LocationRecord lastLocationRecord = currentTeam.getLastLocation();
        if (lastLocationRecord == null) {
            holder.mLastLocationTextView.setVisibility(View.GONE);
        } else {
            holder.mLastLocationTextView.setVisibility(View.VISIBLE);
            String locationInfo = locationInfoProvider.getLocationInfo(lastLocationRecord);
            holder.mLastLocationTextView.setText(
                    mContext.getString(R.string.msg_last_location_record_received_adapter, locationInfo)
            );
        }
        holder.itemView.setOnClickListener(view ->
                mOnTeamHintSelectedListener.onTeamHintClick(currentTeam.getTeamNumber()));
    }

    @Override
    public int getItemCount() {
        return mActualTeams.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void setupTeams(List<Team> teams) {
        mAllTeams = teams;
        notifyDataSetChanged();
    }

    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(BUNDLE_ALL_TEAM_LIST, mAllTeams.toArray(new Team[mAllTeams.size()]));
        bundle.putParcelableArray(BUNDLE_ACTUAL_TEAM_LIST, mActualTeams.toArray(new Team[mActualTeams.size()]));
        return bundle;
    }

    public void onRestoreInstanceState(Bundle savedAdapterState) {
        Parcelable[] allTeamsParcelables = savedAdapterState.getParcelableArray(BUNDLE_ALL_TEAM_LIST);
        Parcelable[] actualTeamsParcelables = savedAdapterState.getParcelableArray(BUNDLE_ACTUAL_TEAM_LIST);
        if (allTeamsParcelables != null && actualTeamsParcelables != null) {
            ArrayList<Team> allTeams = new ArrayList<>(allTeamsParcelables.length);
            for (Parcelable parcelable : allTeamsParcelables) {
                allTeams.add((Team) parcelable);
            }
            ArrayList<Team> actualTeams = new ArrayList<>(actualTeamsParcelables.length);
            for (Parcelable parcelable : actualTeamsParcelables) {
                actualTeams.add((Team) parcelable);
            }
            mAllTeams = allTeams;
            mActualTeams = actualTeams;
            notifyDataSetChanged();
        }
    }

    public interface OnTeamHintSelectedListener {

        void onTeamHintClick(long teamNumber);
    }

    public interface TeamFilterResultsListener {

        void onTeamHintIsEmpty(boolean isEmpty);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_team_id) TextView mTeamIdTextView;
        @BindView(R.id.tv_last_location) TextView mLastLocationTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class SearchTeamViewFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                List<Team> filteredItems = collectFilteredItems(filterPattern);
                results.values = filteredItems;
                results.count = filteredItems.size();
            } else {
                results.values = mAllTeams;
                results.count = mAllTeams.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mActualTeams = (List<Team>) results.values;
            mTeamFilterResultsListener.onTeamHintIsEmpty(mActualTeams.isEmpty());
            notifyDataSetChanged();
        }

        private List<Team> collectFilteredItems(String filterPattern) {
            List<Team> filteredTeamList = new ArrayList<>();
            //noinspection Convert2streamapi
            for (Team team : mAllTeams) {
                if (Long.toString(team.getTeamNumber()).startsWith(filterPattern)) {
                    filteredTeamList.add(team);
                }
            }
            return filteredTeamList;
        }
    }
}
