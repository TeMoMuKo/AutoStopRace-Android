package pl.temomuko.autostoprace.ui.teamslocationsmap.adapter.searchteamview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.List;

import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.Team;

/**
 * Created by Rafał Naniewicz on 23.04.2016.
 */
public class SearchTeamView extends EditText {

    private static final int UNDEFINED_ATTR = -1;
    private static final String BUNDLE_ADAPTER_TEAMS = "bundle_adapter_teams";

    private SearchTeamHintsAdapter mSearchTeamHintsAdapter;
    private RecyclerView mRecyclerView;
    private View mOptionalHintsView;
    private OnTeamRequestedListener mOnTeamRequestedListener;
    private boolean mTeamHintsEmpty = true;

    public SearchTeamView(Context context) {
        this(context, null);
    }

    public SearchTeamView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public SearchTeamView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupDefaultAttributes(context, attrs);
        initialize(context);
    }

    private void setupDefaultAttributes(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.SearchTeamViewOld);

        final int imeOptions = a.getInt(R.styleable.EditText_android_imeOptions, UNDEFINED_ATTR);
        setImeOptions(imeOptions == UNDEFINED_ATTR ? EditorInfo.IME_ACTION_SEARCH : imeOptions);

        final Drawable backgroundOptions = a.getDrawable(R.styleable.EditText_android_background);
        setBackground(backgroundOptions == null ? null : backgroundOptions);

        a.recycle();
    }

    private void initialize(Context context) {
        mSearchTeamHintsAdapter = new SearchTeamHintsAdapter(context,
                this::handleTeamHintClick,
                this::handleIsTeamFilterResultEmpty);
        setTextChangeListener();
    }

    private void handleTeamHintClick(int teamNumber) {
        String teamNumberText = String.valueOf(teamNumber);
        setText(teamNumberText);
        setSelection(teamNumberText.length());
        closeSearch();
        mOnTeamRequestedListener.onTeamRequest(teamNumber);
    }

    private void handleIsTeamFilterResultEmpty(boolean isTeamFilterResultEmpty) {
        mTeamHintsEmpty = isTeamFilterResultEmpty;
        if (isTeamFilterResultEmpty) {
            setHintsVisibility(GONE);
        } else {
            if (hasFocus()) {
                setHintsVisibility(VISIBLE);
            }
        }
    }

    private void setTextChangeListener() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTeamHints(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //no-op
            }
        });
    }

    /* EditText methods */

    @Override
    public void onEditorAction(int actionCode) {
        if (actionCode == EditorInfo.IME_ACTION_SEARCH || actionCode == EditorInfo.IME_ACTION_DONE) {
            requestSearch();
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setHintsVisibility(focused && !mTeamHintsEmpty ? VISIBLE : GONE);
    }

    /* SearchTeamView methods*/

    public Bundle saveHintsState() {
        Bundle bundle = new Bundle();
        if (mSearchTeamHintsAdapter != null) {
            bundle.putBundle(BUNDLE_ADAPTER_TEAMS, mSearchTeamHintsAdapter.onSaveInstanceState());
        }
        return bundle;
    }

    public void restoreHintState(Bundle searchTeamBundle) {
        Bundle searchTeamHintsState = searchTeamBundle.getBundle(BUNDLE_ADAPTER_TEAMS);
        if (searchTeamHintsState != null && mSearchTeamHintsAdapter != null) {
            mSearchTeamHintsAdapter.onRestoreInstanceState(searchTeamHintsState);
        }
    }

    public void requestSearch() {
        mOnTeamRequestedListener.onTeamRequest(getText().toString());
        closeSearch();
    }

    public void setHintsRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mSearchTeamHintsAdapter);
    }

    public void setOptionalHintsView(View hintsView) {
        mOptionalHintsView = hintsView;
    }

    public void setHints(List<Team> teams) {
        mSearchTeamHintsAdapter.setupTeams(teams);
        filterTeamHints(getText());
    }

    public void setOnTeamRequestedListener(OnTeamRequestedListener onTeamRequestedListener) {
        mOnTeamRequestedListener = onTeamRequestedListener;
    }

    public void openSearch() {
        requestFocus();
        setSelection(getText().length());
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(this, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /* Private helper methods */

    private void setHintsVisibility(int visibility) {
        if (mOptionalHintsView == null) {
            mRecyclerView.setVisibility(visibility);
        } else {
            mOptionalHintsView.setVisibility(visibility);
        }
    }

    private void closeSearch() {
        clearFocus();
        ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getWindowToken(), 0);
    }

    private void filterTeamHints(CharSequence text) {
        if (mRecyclerView != null && mSearchTeamHintsAdapter != null) {
            mRecyclerView.scrollToPosition(0);
            mSearchTeamHintsAdapter.getFilter().filter(text);
        }
    }

    public interface OnTeamRequestedListener {

        void onTeamRequest(int teamId);

        void onTeamRequest(String teamString);
    }
}
