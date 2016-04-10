package pl.temomuko.autostoprace.ui.phrasebook;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import pl.temomuko.autostoprace.R;
import pl.temomuko.autostoprace.data.model.Phrasebook;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity;
import pl.temomuko.autostoprace.ui.phrasebook.adapter.PhrasebookAdapter;
import pl.temomuko.autostoprace.ui.widget.VerticalDividerItemDecoration;

/**
 * Created by Szymon Kozak on 2016-02-05.
 */
public class PhrasebookActivity extends DrawerActivity implements PhrasebookMvpView {

    @Inject PhrasebookPresenter mPhrasebookPresenter;
    @Inject PhrasebookAdapter mPhrasebookAdapter;
    @Inject VerticalDividerItemDecoration mVerticalDividerItemDecoration;
    @Bind(R.id.rv_phrasebook) RecyclerView mPhrasebookRecyclerView;
    @Bind(R.id.spinner_lang) AppCompatSpinner mLangSpinner;
    private ArrayAdapter<CharSequence> mLangSpinnerAdapter;

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
    }

    @Override
    protected void onDestroy() {
        mPhrasebookPresenter.detachView();
        super.onDestroy();
    }

    private void setupSpinner() {
        mLangSpinnerAdapter = new ArrayAdapter<>(this, R.layout.item_spinner);
        mLangSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLangSpinner.setAdapter(mLangSpinnerAdapter);
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mPhrasebookRecyclerView.setLayoutManager(linearLayoutManager);
        mPhrasebookRecyclerView.setAdapter(mPhrasebookAdapter);
        mPhrasebookRecyclerView.addItemDecoration(mVerticalDividerItemDecoration);
    }

    /* MVP View methods */

    @Override
    public void updatePhrasebookData(int languagePosition, List<Phrasebook.Item> phraseItems) {
        mPhrasebookAdapter.setLanguagePosition(languagePosition);
        mPhrasebookAdapter.setPhrasebookItems(phraseItems);
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
                changePhrasebookLanguage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //no-op
            }
        });
        mLangSpinnerAdapter.notifyDataSetChanged();
    }

    private void changePhrasebookLanguage(int position) {
        mPhrasebookPresenter.saveCurrentLanguagePosition(position);
        mPhrasebookAdapter.setLanguagePosition(position);
        mPhrasebookAdapter.notifyDataSetChanged();
    }
}
