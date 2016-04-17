package pl.temomuko.autostoprace.data.local.csv;

import java.util.ArrayList;
import java.util.List;

import pl.temomuko.autostoprace.data.model.ContactRow;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public final class ContactCsvRowsParserUtil {

    private static final int TYPE_ROW = 0;
    private static final int CONTENT_ROW = 1;
    private static final int OPTIONAL_DISPLAYED_CONTENT = 2;
    private static final int DESCRIPTION_ROW = 3;
    private static final String TYPE_ROW_NAME = "TYPE";
    private static final String CONTENT_ROW_NAME = "CONTENT";
    private static final String OPTIONAL_DISPLAYED_CONTENT_ROW_NAME = "OPTIONAL_DISPLAYED_CONTENT";
    private static final String DESCRIPTION_ROW_NAME = "DESCRIPTION";

    private ContactCsvRowsParserUtil() {
        throw new AssertionError();
    }

    public static List<ContactRow> createContactRowsFromCsvRows(List<String[]> csvRows) throws CsvParseException {
        List<ContactRow> contactRows = new ArrayList<>(csvRows.size());
        csvRows = formatCsv(csvRows);
        for (String[] csvRow : csvRows) {
            contactRows.add(createContactRowFromFormatedCsvRows(csvRow));
        }
        return contactRows;
    }

    //TYPE,CONTENT,OPTIONAL_DISPLAYED_CONTENT,DESCRIPTION
    private static List<String[]> formatCsv(List<String[]> csvRows) throws CsvParseException {
        try {
            if (csvRows.isEmpty()) {
                throw new CsvParseException("Check csv file format, first line not found");
            }
            String[] firstCsvRow = csvRows.remove(0);
            boolean properFirstLineFormat = (firstCsvRow[TYPE_ROW].equals(TYPE_ROW_NAME) &&
                    firstCsvRow[CONTENT_ROW].equals(CONTENT_ROW_NAME) &&
                    firstCsvRow[OPTIONAL_DISPLAYED_CONTENT].equals(OPTIONAL_DISPLAYED_CONTENT_ROW_NAME) &&
                    firstCsvRow[DESCRIPTION_ROW].equals(DESCRIPTION_ROW_NAME));
            if (!properFirstLineFormat) {
                throw new CsvParseException("Check column name");
            }
            return csvRows;
        } catch (IndexOutOfBoundsException e) {
            throw new CsvParseException("Wrong csv formatting", e);
        }
    }

    private static ContactRow createContactRowFromFormatedCsvRows(String[] csvRow) throws CsvParseException {
        try {
            if (csvRow[OPTIONAL_DISPLAYED_CONTENT].isEmpty()) {
                return new ContactRow(csvRow[TYPE_ROW], csvRow[CONTENT_ROW], csvRow[DESCRIPTION_ROW]);
            } else {
                return new ContactRow(csvRow[TYPE_ROW], csvRow[CONTENT_ROW], csvRow[OPTIONAL_DISPLAYED_CONTENT], csvRow[DESCRIPTION_ROW]);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new CsvParseException("Missing field in CSV", e);
        }
    }
}
