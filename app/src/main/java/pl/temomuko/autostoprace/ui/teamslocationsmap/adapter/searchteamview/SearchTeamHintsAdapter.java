package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.searchteamview;

import android.content.Context;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.LocationRecord;
import pl.temomuko.autostoprace.data.model.Team;

/**
 * Created by Rafał Naniewicz on 23.04.2016.
 */
public class SearchTeamHintsAdapter extends RecyclerView.Adapter<SearchTeamHintsAdapter.ViewHolder>
        implements Filterable {

    private Context mContext;
    private List<Team> mAllTeams;
    private List<Team> mActualTeams;
    private Filter mFilter;
    private OnTeamHintSelectedListener mOnTeamHintSelectedListener;
    private TeamFilterResultsListener mTeamFilterResultsListener;

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
        LocationRecord lastLocationRecord = currentTeam.getLastLocationRecord();
        if (lastLocationRecord == null) {
            holder.mLastLocationTextView.setVisibility(View.GONE);
        } else {
            holder.mLastLocationTextView.setVisibility(View.VISIBLE);
            holder.mLastLocationTextView.setText(mContext.getString(R.string.msg_last_location_record_received_adapter,
                    currentTeam.getLastLocationRecord().getAddress()));
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
        mAllTeams = mActualTeams = teams;
        notifyDataSetChanged();
    }

    public Parcelable[] onSaveInstanceState() {
        return mAllTeams.toArray(
                new Team[mAllTeams.size()]);
    }

    public void onRestoreInstanceState(Parcelable[] searchTeamHintsState) {
        ArrayList<Team> teams = new ArrayList<>(searchTeamHintsState.length);
        for (Parcelable parcelable : searchTeamHintsState) {
            teams.add((Team) parcelable);
        }
        setupTeams(teams);
    }

    public interface OnTeamHintSelectedListener {

        void onTeamHintClick(int teamNumber);
    }

    public interface TeamFilterResultsListener {

        void onTeamHintEmpty();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_team_id) TextView mTeamIdTextView;
        @Bind(R.id.tv_last_location) TextView mLastLocationTextView;

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
            if (mActualTeams.isEmpty()) {
                mTeamFilterResultsListener.onTeamHintEmpty();
            }
            notifyDataSetChanged();
        }

        private List<Team> collectFilteredItems(String filterPattern) {
            List<Team> filteredTeamList = new ArrayList<>();
            for (Team team : mAllTeams) {
                if (Integer.toString(team.getTeamNumber()).startsWith(filterPattern)) {
                    filteredTeamList.add(team);
                }
            }
            return filteredTeamList;
        }
    }
}
