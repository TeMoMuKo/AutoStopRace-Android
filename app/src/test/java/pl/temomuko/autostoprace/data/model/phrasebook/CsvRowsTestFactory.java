package pl.temomuko.autostoprace.data.model.phrasebook;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szymon Kozak on 2016-04-15.
 */
public class CsvRowsTestFactory {

    @NonNull
    public static List<String[]> getCsvRows() {
        List<String[]> csvRows = new ArrayList<>();
        String[] languagesContent = new String[]{"polski", "angielski", "niemiecki", "włoski"};
        String[] translationsContent = new String[]{"Cześć", "Hi", "hallo", "Ciao"};
        csvRows.add(languagesContent);
        csvRows.add(translationsContent);
        return csvRows;
    }

}
