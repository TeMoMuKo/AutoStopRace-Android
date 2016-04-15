package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.searchteamview;

import android.support.annotation.NonNull;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import pl.temomuko.autostoprace.data.model.Team;
import pl.temomuko.autostoprace.util.LogUtil;

/**
 * Created by Rafał Naniewicz on 09.04.2016.
 */
public class SearchTeamFilter extends Filter {

    private static final String TAG = SearchTeamFilter.class.getSimpleName();

    private SearchTeamViewAdapter mSearchTeamViewAdapter;
    private List<Team> mOriginalTeamList;
    private List<Team> mFilteredTeamList;

    public SearchTeamFilter(SearchTeamViewAdapter searchTeamViewAdapter, @NonNull List<Team> originalTeamList) {
        LogUtil.i(TAG, "New filter created");
        mSearchTeamViewAdapter = searchTeamViewAdapter;
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
                if (Integer.toString(team.getTeamNumber()).startsWith(filterPattern)) {
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
        if (mSearchTeamViewAdapter != null) {
            //noinspection unchecked
            mSearchTeamViewAdapter.replaceFilteredTeamList((List<Team>) results.values);
        }
    }
}
