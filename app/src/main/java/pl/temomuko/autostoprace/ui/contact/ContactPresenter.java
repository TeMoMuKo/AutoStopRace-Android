package pl.temomuko.autostoprace.ui.contact;

import java.util.List;

import javax.inject.Inject;

import pl.temomuko.autostoprace.data.DataManager;
import pl.temomuko.autostoprace.data.model.ContactField;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerBasePresenter;
import pl.temomuko.autostoprace.util.rx.RxUtil;
import rx.Subscription;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public class ContactPresenter extends DrawerBasePresenter<ContactMvpView> {

    private static final String TAG = ContactPresenter.class.getSimpleName();

    private Subscription mLoadContactDataSubscription;

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
        if (mLoadContactDataSubscription != null) mLoadContactDataSubscription.unsubscribe();
        super.detachView();
    }

    public void loadContactData() {
        mLoadContactDataSubscription = mDataManager.getContactFields()
                .compose(RxUtil.applySingleIoSchedulers())
                .subscribe(this::handleContactFields);
    }

    private void handleContactFields(List<ContactField> contactFields) {
        if (!contactFields.isEmpty()) {
            getMvpView().setUpFab(contactFields.remove(0));
        }
        getMvpView().setContactRows(contactFields);
    }
}
