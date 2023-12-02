package bg.sofia.uni.fmi.mjt.csvprocessor.exceptions;

import java.io.IOException;

public class CsvDataNotCorrectException extends Exception {
    public CsvDataNotCorrectException(String message) {
        super(message);
    }

    public CsvDataNotCorrectException(String errorReadingCsvData, IOException e) {
        super(errorReadingCsvData, e);
    }
}
