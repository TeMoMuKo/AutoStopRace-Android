package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.searchteamview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.Team;

/**
 * Created by Rafał on 15.04.2016.
 */
public class SearchTeamView extends AutoCompleteTextView
        implements SearchTeamViewAdapter.OnTeamHintSelectedListener {

    private static final int DEFAULT_THRESHOLD = 1;
    private static final int UNDEFINED_ATTR = -1;

    SearchTeamViewAdapter mSearchTeamViewAdapter;
    private OnTeamRequestedListener mOnTeamRequestedListener;

    public interface OnTeamRequestedListener {

        void onTeamRequest(int teamId);

        void onTeamRequest(String teamString);
    }

    public SearchTeamView(Context context) {
        this(context, null);
    }

    public SearchTeamView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.autoCompleteTextViewStyle);
    }

    public SearchTeamView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
        setupDefaultAttributes(context, attrs);
    }

    private void setupDefaultAttributes(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.SearchTeamView);

        final int imeOptions = a.getInt(R.styleable.SearchTeamView_android_imeOptions, -1);
        setImeOptions(imeOptions == UNDEFINED_ATTR ? EditorInfo.IME_ACTION_SEARCH : imeOptions);

        final int thresholdOptions = a.getInt(R.styleable.SearchTeamView_android_completionThreshold, -1);
        setThreshold(thresholdOptions == UNDEFINED_ATTR ? DEFAULT_THRESHOLD : thresholdOptions);

        final Drawable backgroundOptions = a.getDrawable(R.styleable.SearchTeamView_android_background);
        setBackground(backgroundOptions == null ? null : backgroundOptions);

        a.recycle();
    }

    private void initialize(Context context) {
        mSearchTeamViewAdapter = new SearchTeamViewAdapter(context);
        mSearchTeamViewAdapter.setTeamSelectedListener(this);
        setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                mOnTeamRequestedListener.onTeamRequest(getText().toString());
                closeSearch();
                return true;
            }
            return false;
        });
    }

    public void setOnTeamRequestedListener(OnTeamRequestedListener onTeamRequestedListener) {
        mOnTeamRequestedListener = onTeamRequestedListener;
    }

    public Parcelable[] saveHintState() {
        return mSearchTeamViewAdapter.getOriginalTeamList().toArray(
                new Team[mSearchTeamViewAdapter.getOriginalTeamList().size()]);
    }

    public void restoreHintState(Parcelable[] hintsParcel) {
        ArrayList<Team> mHints = new ArrayList<>(hintsParcel.length);
        for (Parcelable parcelable : hintsParcel) {
            mHints.add((Team) parcelable);
        }
        mSearchTeamViewAdapter.setOriginalTeamList(mHints);
    }

    public void closeSearch() {
        clearFocus();
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getWindowToken(), 0);
    }

    public void setHints(List<Team> teamHints) {
        mSearchTeamViewAdapter.setOriginalTeamList(teamHints);
        setAdapter(mSearchTeamViewAdapter);
    }

    @Override
    public void onTeamHintClick(int teamId) {
        closeSearch();
        setText(String.valueOf(teamId));
        mOnTeamRequestedListener.onTeamRequest(teamId);
    }
}
