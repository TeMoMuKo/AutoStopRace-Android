package pl.temomuko.autostoprace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.Phrasebook;
import pl.temomuko.autostoprace.data.model.phrasebook.CsvRowsTestFactory;
import pl.temomuko.autostoprace.ui.phrasebook.PhrasebookMvpView;
import pl.temomuko.autostoprace.ui.phrasebook.PhrasebookPresenter;
import pl.temomuko.autostoprace.util.RxSchedulersOverrideRule;
import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Szymon Kozak on 2016-04-10.
 */
@RunWith(MockitoJUnitRunner.class)
public class PhrasebookPresenterTest {

    public static final int LANGUAGE_SPINNER_POSITION = 0;
    public static final String FAKE_QUERY = "fake_query";
    public static final String EMPTY_FAKE_QUERY = "";

    //TODO: write tests

    @Mock PhrasebookMvpView mMockPhrasebookMvpView;
    @Mock DataManager mMockDataManager;
    private PhrasebookPresenter mPhrasebookPresenter;

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Before
    public void setUp() throws Exception {
        mPhrasebookPresenter = new PhrasebookPresenter(mMockDataManager);
        mPhrasebookPresenter.attachView(mMockPhrasebookMvpView);
    }

    @After
    public void tearDown() throws Exception {
        mPhrasebookPresenter.detachView();
    }

    @Test
    public void testLoadPhrasebook() throws Exception {
        //given
        Phrasebook fakePhrasebook = Phrasebook.createFromCsvRows(CsvRowsTestFactory.getCsvRows(), 0, 0);
        when(mMockDataManager.getPhrasebook()).thenReturn(Observable.just(fakePhrasebook));
        when(mMockDataManager.getCurrentPhrasebookLanguagePosition()).thenReturn(LANGUAGE_SPINNER_POSITION);

        //when
        mPhrasebookPresenter.loadPhrasebook();

        //then
        verify(mMockPhrasebookMvpView).updateSpinner(LANGUAGE_SPINNER_POSITION, fakePhrasebook.getLanguagesHeader());
        verify(mMockPhrasebookMvpView).updatePhrasebookData(LANGUAGE_SPINNER_POSITION, fakePhrasebook.getPhraseItems());
    }

    @Test
    public void testHandleSearchQuery() throws Exception {
        //when
        mPhrasebookPresenter.handleSearchQuery(FAKE_QUERY);

        //then
        verify(mMockPhrasebookMvpView).filterPhrases(FAKE_QUERY);
    }

    @Test
    public void testHandleSearchEmptyQuery() throws Exception {
        //when
        mPhrasebookPresenter.handleSearchQuery(EMPTY_FAKE_QUERY);

        //then
        verify(mMockPhrasebookMvpView).clearPhrasesFilter();
    }

    @Test
    public void testChangePhrasebookLanguage() throws Exception {
        //when
        mPhrasebookPresenter.changePhrasebookLanguage(LANGUAGE_SPINNER_POSITION);

        //then
        verify(mMockDataManager).saveCurrentPhrasebookLanguagePosition(LANGUAGE_SPINNER_POSITION);
        verify(mMockPhrasebookMvpView).changePhrasebookLanguage(LANGUAGE_SPINNER_POSITION);
    }
}