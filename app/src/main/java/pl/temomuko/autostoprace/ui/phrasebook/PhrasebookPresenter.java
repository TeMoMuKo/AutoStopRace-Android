package pl.temomuko.autostoprace.ui.phrasebook;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.Phrasebook;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Subscription;

/**
 * Created by Szymon Kozak on 2016-04-09.
 */
public class PhrasebookPresenter extends DrawerBasePresenter<PhrasebookMvpView> {

    private Subscription mSubscription;

    @Inject
    public PhrasebookPresenter(DataManager dataManager) {
        super(dataManager);
    }

    public void loadPhrasebook() {
        mSubscription = mDataManager.getPhrasebook()
                .compose(RxUtil.applyIoSchedulers())
                .subscribe(this::handlePhrasebook);
    }

    private void handlePhrasebook(Phrasebook phrasebook) {
        int languagePosition = mDataManager.getCurrentPhrasebookLanguagePosition();
        getMvpView().updateSpinner(languagePosition, phrasebook.getLanguagesHeader());
        getMvpView().updatePhrasebookData(languagePosition, phrasebook.getPhraseItems());
    }

    @Override
    public void detachView() {
        if (mSubscription != null) mSubscription.unsubscribe();
        super.detachView();
    }

    public void handleSearchQuery(String query) {
        if (query.length() > 0) {
            getMvpView().filterPhrases(query);
        } else {
            getMvpView().clearPhrasesFilter();
        }
    }

    public void changePhrasebookLanguage(int position) {
        mDataManager.saveCurrentPhrasebookLanguagePosition(position);
        getMvpView().changePhrasebookLanguage(position);
    }
}
