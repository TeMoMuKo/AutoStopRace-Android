package pl.temomuko.autostoprace.ui.contact;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.ContactRow;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Subscription;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public class ContactPresenter extends DrawerBasePresenter<ContactMvpView> {

    private static final String TAG = ContactPresenter.class.getSimpleName();

    private Subscription mLoadContactRowsSubscription;

    @Inject
    public ContactPresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void attachView(ContactMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        if (mLoadContactRowsSubscription != null) mLoadContactRowsSubscription.unsubscribe();
        super.detachView();
    }

    public void loadContactRows() {
        mLoadContactRowsSubscription = mDataManager.getContactRows()
                .compose(RxUtil.applySingleIoSchedulers())
                .subscribe(this::handleContactRows);
    }

    private void handleContactRows(List<ContactRow> contactRows) {
    }
}
