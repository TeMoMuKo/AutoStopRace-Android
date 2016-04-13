package pl.temomuko.autostoprace.ui.teamslocations.adapter;

import android.support.annotation.NonNull;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.util.LogUtil;

/**
 * Created by Rafał Naniewicz on 09.04.2016.
 */
public class TeamsFilter extends Filter {

    private static final String TAG = TeamsFilter.class.getSimpleName();

    private AutoCompleteTeamsAdapter mAutoCompleteTeamsAdapter;
    private List<Team> mOriginalTeamList;
    private List<Team> mFilteredTeamList;

    public TeamsFilter(AutoCompleteTeamsAdapter autoCompleteTeamsAdapter, @NonNull List<Team> originalTeamList) {
        LogUtil.i(TAG, "New filter created");
        mAutoCompleteTeamsAdapter = autoCompleteTeamsAdapter;
        mOriginalTeamList = originalTeamList;
        mFilteredTeamList = new ArrayList<>(mOriginalTeamList.size());
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        mFilteredTeamList.clear();
        final FilterResults results = new FilterResults();
        if (constraint == null || constraint.length() == 0) {
            mFilteredTeamList.addAll(mOriginalTeamList);
        } else {
            final String filterPattern = constraint.toString().toLowerCase().trim();
            for (Team team : mOriginalTeamList) {
                if (Integer.toString(team.getId()).startsWith(filterPattern)) {
                    mFilteredTeamList.add(team);
                }
            }
        }
        results.values = mFilteredTeamList;
        results.count = mFilteredTeamList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        if (mAutoCompleteTeamsAdapter != null) {
            //noinspection unchecked
            mAutoCompleteTeamsAdapter.replaceFilteredTeamList((List<Team>) results.values);
        }
    }
}
