package pl.temomuko.autostoprace.data.local.csv;

import java.util.ArrayList;
import java.util.List;

import pl.temomuko.autostoprace.data.model.ContactField;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public final class ContactCsvRowsParserUtil {

    private static final int TYPE_ROW = 0;
    private static final int VALUE_ROW = 1;
    private static final int OPTIONAL_DISPLAYED_VALUE = 2;
    private static final int DESCRIPTION_ROW = 3;
    private static final String TYPE_ROW_NAME = "TYPE";
    private static final String VALUE_ROW_NAME = "VALUE";
    private static final String OPTIONAL_DISPLAYED_VALUE_ROW_NAME = "OPTIONAL_DISPLAYED_VALUE";
    private static final String DESCRIPTION_ROW_NAME = "DESCRIPTION";

    private ContactCsvRowsParserUtil() {
        throw new AssertionError();
    }

    public static List<ContactField> createContactRowsFromCsvRows(List<String[]> csvRows) throws CsvParseException {
        List<ContactField> contactFields = new ArrayList<>(csvRows.size());
        csvRows = formatCsv(csvRows);
        for (String[] csvRow : csvRows) {
            contactFields.add(createContactRowFromFormattedCstRows(csvRow));
        }
        return contactFields;
    }

    private static List<String[]> formatCsv(List<String[]> csvRows) throws CsvParseException {
        try {
            if (csvRows.isEmpty()) {
                throw new CsvParseException("Check csv file format, first line not found");
            }
            String[] firstCsvRow = csvRows.remove(0);
            boolean properFirstLineFormat = (firstCsvRow[TYPE_ROW].equals(TYPE_ROW_NAME) &&
                    firstCsvRow[VALUE_ROW].equals(VALUE_ROW_NAME) &&
                    firstCsvRow[OPTIONAL_DISPLAYED_VALUE].equals(OPTIONAL_DISPLAYED_VALUE_ROW_NAME) &&
                    firstCsvRow[DESCRIPTION_ROW].equals(DESCRIPTION_ROW_NAME));
            if (!properFirstLineFormat) {
                throw new CsvParseException("Check column names");
            }
            return csvRows;
        } catch (IndexOutOfBoundsException e) {
            throw new CsvParseException("Wrong csv formatting", e);
        }
    }

    private static ContactField createContactRowFromFormattedCstRows(String[] csvRow) throws CsvParseException {
        try {
            if (csvRow[OPTIONAL_DISPLAYED_VALUE].isEmpty()) {
                return new ContactField(csvRow[TYPE_ROW], csvRow[VALUE_ROW], csvRow[DESCRIPTION_ROW]);
            } else {
                return new ContactField(csvRow[TYPE_ROW], csvRow[VALUE_ROW], csvRow[OPTIONAL_DISPLAYED_VALUE], csvRow[DESCRIPTION_ROW]);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new CsvParseException("Missing field in csv", e);
        }
    }
}
