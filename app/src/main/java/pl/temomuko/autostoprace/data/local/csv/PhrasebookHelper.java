package pl.temomuko.autostoprace.data.local.csv;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.model.Phrasebook;
import pl.temomuko.autostoprace.injection.AppContext;
import rx.Observable;

/**
 * Created by Szymon Kozak on 2016-04-09.
 */
@Singleton
public class PhrasebookHelper {

    private AssetManager mAssetManager;
    private Phrasebook mPhrasebook;

    @Inject
    public PhrasebookHelper(@AppContext Context context) {
        mAssetManager = context.getAssets();
    }

    public Observable<Phrasebook> getPhrasebook() {
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(receivePhrasebook());
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private Phrasebook receivePhrasebook() throws IOException {
        if (mPhrasebook == null) {
            loadPhrasebookFromCsv();
        }
        return mPhrasebook;
    }

    private void loadPhrasebookFromCsv() throws IOException {
        InputStream csvStream = mAssetManager.open(Constants.PHRASEBOOK_CSV_ASSET_PATH);
        List<String[]> csvRows = CsvUtil.getRowsFromStream(csvStream);
        mPhrasebook = Phrasebook.createFromCsvRows(
                csvRows,
                Constants.LANGUAGES_HEADER_ROW_POSITION,
                Constants.ORIGINAL_LANG_COLUMN_POSITION
        );
    }
}
