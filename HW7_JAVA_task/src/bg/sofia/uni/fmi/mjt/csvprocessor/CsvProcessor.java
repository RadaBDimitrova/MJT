package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.MarkdownTablePrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class CsvProcessor implements CsvProcessorAPI {
    Table table;

    public CsvProcessor() {
        this(new BaseTable());
    }

    public CsvProcessor(Table table) {
        this.table = table;
    }

    @Override
    public void readCsv(Reader reader, String delimiter) throws CsvDataNotCorrectException {
        try (var bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split("\\Q" + delimiter + "\\E");
                table.addData(data);
            }
        } catch (IOException e) {
            throw new CsvDataNotCorrectException("Error reading CSV data", e);
        }
    }

    @Override
    public void writeTable(Writer writer, ColumnAlignment... alignments) {
        var tablePrinter = new MarkdownTablePrinter();
        int rows = table.getRowsCount();
        int ctr = 0;
        try {
            for (String row : tablePrinter.printTable(table, alignments)) {
                writer.write(row);
                ctr++;
                if (ctr <= rows) {
                    writer.write(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error while writing table.");
        }
    }
}