package pl.temomuko.autostoprace.data.local.csv;

import java.io.IOException;

/**
 * Created by Rafał Naniewicz on 17.04.2016.
 */
public class CsvParseException extends IOException {

    public CsvParseException(String detailMessage) {
        super(detailMessage);
    }

    public CsvParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
