package pl.temomuko.autostoprace.ui.contact;

import java.util.List;

import pl.temomuko.autostoprace.data.model.ContactRow;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public interface ContactMvpView extends DrawerMvpView {

    void setContactRows(List<ContactRow> contactRows);
}
