package pl.temomuko.autostoprace.data.local.csv;

import android.content.Context;
import android.content.res.AssetManager;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.temomuko.autostoprace.Constants;
import pl.temomuko.autostoprace.data.model.Phrasebook;
import pl.temomuko.autostoprace.injection.AppContext;
import rx.Single;

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

    public Single<Phrasebook> getPhrasebook() {
        return Single.create(singleSubscriber -> {
            try {
                singleSubscriber.onSuccess(receivePhrasebook());
            } catch (IOException e) {
                singleSubscriber.onError(e);
            }
        });
    }

    private synchronized Phrasebook receivePhrasebook() throws IOException {
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
