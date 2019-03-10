package pl.temomuko.autostoprace.ui.contact;

import java.util.List;

import pl.temomuko.autostoprace.domain.model.ContactField;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public interface ContactMvpView extends DrawerMvpView {

    void setContactRows(List<ContactField> contactFields);

    void setUpFab(ContactField fabContactField);
}
