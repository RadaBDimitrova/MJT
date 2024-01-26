package bg.sofia.uni.fmi.mjt.csvprocessor;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

import static bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment.CENTER;
import static bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment.LEFT;
import static bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment.RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVProcessorTest {

    @Test
    public void testReadCsv() throws CsvDataNotCorrectException {
        CsvProcessor csvProcessor = new CsvProcessor();
        String csvData = "Name, Age, FN\nRada, 20, 8MI000\nDiana, 23, 6MI000";

        try (StringReader reader = new StringReader(csvData)) {
            csvProcessor.readCsv(reader, ", ");
        }

        Table table = csvProcessor.table;
        assertEquals(3, table.getColumnNames().size());
        assertEquals(3, table.getRowsCount());
        assertEquals(Arrays.asList("Name", "Age", "FN"), new ArrayList<>(table.getColumnNames()));
        assertEquals(Arrays.asList("Rada", "Diana"), new ArrayList<>(table.getColumnData("Name")));
        assertEquals(Arrays.asList("20", "23"), new ArrayList<>(table.getColumnData("Age")));
        assertEquals(Arrays.asList("8MI000", "6MI000"), new ArrayList<>(table.getColumnData("FN")));
    }

    @Test
    public void testWriteTable() {
        StringWriter writer = new StringWriter();
        CsvProcessor csvProcessor = new CsvProcessor(new BaseTable());
        Table table = csvProcessor.table;
        try {
            table.addData(new String[]{"Name", "Age", "FN"});
            table.addData(new String[]{"Rada", "20", "8MI000"});
            table.addData(new String[]{"Diana", "23", "6MI000"});
        } catch (CsvDataNotCorrectException e) {
            throw new RuntimeException(e);
        }
        csvProcessor.writeTable(writer, LEFT, RIGHT, CENTER);

        String result =
                "| Name  | Age |   FN   |" + System.lineSeparator() +
                "| :---- | --: | :----: |" + System.lineSeparator() +
                "| Rada  | 20  | 8MI000 |" + System.lineSeparator() +
                "| Diana | 23  | 6MI000 |";

        assertEquals(result, writer.toString());
    }
}
