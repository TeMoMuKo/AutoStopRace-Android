package pl.temomuko.autostoprace.data.local.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by Szymon Kozak on 2016-04-09.
 */
public final class CsvUtil {

    private CsvUtil() {
        throw new AssertionError();
    }

    public static List<String[]> getRowsFromStream(InputStream csvStream) throws IOException {
        ArrayList<String[]> csvRows = new ArrayList<>();
        InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
        CSVReader csvReader = new CSVReader(csvStreamReader);
        String[] row;
        while ((row = csvReader.readNext()) != null) {
            csvRows.add(row);
        }
        csvReader.close();
        csvStreamReader.close();
        return csvRows;
    }
}
