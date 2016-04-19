package pl.temomuko.autostoprace.data.local.csv;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.model.ContactField;
import pl.temomuko.autostoprace.injection.AppContext;
import rx.Single;

/**
 * Created by Rafał Naniewicz on 2016-04-09.
 */
@Singleton
public class ContactHelper {

    private AssetManager mAssetManager;
    private List<ContactField> mContactFields;

    @Inject
    public ContactHelper(@AppContext Context context) {
        mAssetManager = context.getAssets();
    }

    public Single<List<ContactField>> getContacts() {
        return Single.create(singleSubscriber -> {
            try {
                singleSubscriber.onSuccess(receiveContacts());
            } catch (IOException e) {
                singleSubscriber.onError(e);
            }
        });
    }

    private synchronized List<ContactField> receiveContacts() throws IOException {
        if (mContactFields == null) {
            loadContactsFromCsv();
        }
        return new ArrayList<>(mContactFields);
    }

    private void loadContactsFromCsv() throws IOException {
        InputStream csvStream = mAssetManager.open(Constants.CONTACT_CSV_ASSET_PATH);
        List<String[]> csvRows = CsvUtil.getRowsFromStream(csvStream);
        mContactFields = ContactCsvRowsParserUtil.createContactRowsFromCsvRows(csvRows);
    }
}
