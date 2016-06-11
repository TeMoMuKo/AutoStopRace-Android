package pl.temomuko.autostoprace.ui.phrasebook;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.Phrasebook;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.phrasebook.adapter.PhrasebookAdapter;
import pl.temomuko.autostoprace.ui.widget.VerticalDividerItemDecoration;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class PhrasebookActivity extends DrawerActivity implements PhrasebookMvpView {

    public static final String BUNDLE_SEARCH_QUERY = "search_query";
    private static final boolean SUBMIT_QUERY = true;

    @Inject PhrasebookPresenter mPhrasebookPresenter;
    @Inject VerticalDividerItemDecoration mVerticalDividerItemDecoration;

    @BindView(R.id.rv_phrasebook) RecyclerView mPhrasebookRecyclerView;
    @BindView(R.id.spinner_lang) AppCompatSpinner mLangSpinner;
    @BindView(R.id.tv_empty) TextView mEmptyResultsTextView;

    private PhrasebookAdapter mPhrasebookAdapter;
    private SearchView mSearchView;
    private ArrayAdapter<CharSequence> mLangSpinnerAdapter;
    private Subscription mSearchViewBindingSubscription;
    private String mLastSearchQuery;
    private MenuItem mSearchItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrasebook);
        getActivityComponent().inject(this);
        mPhrasebookPresenter.attachView(this);
        mPhrasebookPresenter.setupUserInfoInDrawer();
        setupSpinner();
        setupRecyclerView();
        mPhrasebookPresenter.loadPhrasebook();
        loadLastSearchQuery(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        mPhrasebookPresenter.detachView();
        destroySearchView();
        if (mSearchViewBindingSubscription != null) mSearchViewBindingSubscription.unsubscribe();
        super.onDestroy();
    }

    private void destroySearchView() {
        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(null);
            mSearchItem = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveLastSearchQuery(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phrasebook, menu);
        setupSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupSpinner() {
        mLangSpinnerAdapter = new ArrayAdapter<>(this, R.layout.item_spinner);
        mLangSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLangSpinner.setAdapter(mLangSpinnerAdapter);
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mPhrasebookRecyclerView.setLayoutManager(linearLayoutManager);
        mPhrasebookAdapter = new PhrasebookAdapter(this, this::setEmptyInfoVisible);
        mPhrasebookRecyclerView.setAdapter(mPhrasebookAdapter);
        mPhrasebookRecyclerView.addItemDecoration(mVerticalDividerItemDecoration);
    }

    private void setEmptyInfoVisible(boolean state) {
        mEmptyResultsTextView.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }

    private void loadLastSearchQuery(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mLastSearchQuery = savedInstanceState.getString(BUNDLE_SEARCH_QUERY);
        }
    }

    private void saveLastSearchQuery(Bundle outState) {
        if (mSearchItem != null && mSearchItem.isActionViewExpanded()) {
            outState.putString(BUNDLE_SEARCH_QUERY, mSearchView.getQuery().toString().trim());
        }
    }

    private void setupSearchView(Menu menu) {
        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setQueryHint(getString(R.string.search_hint));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        if (mLastSearchQuery != null) {
            mSearchItem.expandActionView();
            mSearchView.setQuery(mLastSearchQuery, SUBMIT_QUERY);
            filterPhrases(mLastSearchQuery);
        }
        setupSearchBinding();
    }

    private void setupSearchBinding() {
        mSearchViewBindingSubscription = RxSearchView.queryTextChanges(mSearchView)
                .skip(1)
                .debounce(Constants.PHRASEBOOK_FILTER_DEBOUNCE, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .map(String::trim)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mPhrasebookPresenter::handleSearchQuery);
    }

    /* MVP View methods */

    @Override
    public void updatePhrasebookData(int languagePosition, List<Phrasebook.Item> phraseItems) {
        mPhrasebookAdapter.setLanguagePosition(languagePosition);
        mPhrasebookAdapter.setActualPhrasebookItems(phraseItems);
        mPhrasebookAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateSpinner(int languagePosition, Phrasebook.LanguagesHeader languagesHeader) {
        mLangSpinnerAdapter.clear();
        mLangSpinnerAdapter.addAll(languagesHeader.getForeignLanguages());
        mLangSpinner.setSelection(languagePosition);
        mLangSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPhrasebookPresenter.changePhrasebookLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //no-op
            }
        });
        mLangSpinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void changePhrasebookLanguage(int position) {
        mPhrasebookAdapter.setLanguagePosition(position);
        mPhrasebookAdapter.notifyDataSetChanged();
    }

    @Override
    public void filterPhrases(String query) {
        mPhrasebookAdapter.getFilter().filter(query);
        mPhrasebookRecyclerView.scrollToPosition(0);
    }

    @Override
    public void clearPhrasesFilter() {
        setEmptyInfoVisible(false);
        mPhrasebookAdapter.clearFilter();
    }
}
