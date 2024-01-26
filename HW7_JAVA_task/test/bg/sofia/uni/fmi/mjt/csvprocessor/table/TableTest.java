package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.Column;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TableTest {
    @Test
    void testAddInvalidData() {
        Map<String, Column> column = new LinkedHashMap<>(0);
        Table t = new BaseTable();
        String[] dat1 = null;
        String[] dat2 = new String[1];
        String[] dat3 = new String[2];
        dat3[0] = null;
        assertThrows(IllegalArgumentException.class, () -> t.addData(dat1));
        assertThrows(CsvDataNotCorrectException.class, () -> t.addData(dat3));
        assertThrows(CsvDataNotCorrectException.class, () -> t.addData(dat3));
        assertThrows(CsvDataNotCorrectException.class, () -> t.addData(dat2));

    }

    @Test
    void testAddValidData() {
        Map<String, Column> columns = new LinkedHashMap<>();
        Column val = new BaseColumn();
        columns.put("column", val);

        Table table = new BaseTable();
        String[] data = new String[1];
        data[0] = "someValue";

        try {
            table.addData(data);
            assertEquals(1, table.getRowsCount());

        } catch (CsvDataNotCorrectException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetColumnDataValidColumn() {
        Map<String, Column> columns = new LinkedHashMap<>();
        columns.put("column1", new BaseColumn(new HashSet<>(Arrays.asList("data1", "data2"))));
        columns.put("column2", new BaseColumn(new HashSet<>(Arrays.asList("data3", "data4"))));

        Table table = new BaseTable(columns);

        Collection<String> columnData = table.getColumnData("column1");

        assertEquals(new HashSet<>(Arrays.asList("data1", "data2")), new HashSet<>(columnData));
    }

    @Test
    void testAddValidData2() {
        Map<String, Column> columns = new LinkedHashMap<>();
        columns.put("column1", new BaseColumn(new HashSet<>(Arrays.asList("data1", "data2"))));
        columns.put("column2", new BaseColumn(new HashSet<>(Arrays.asList("data3", "data4"))));

        Table table = new BaseTable(columns);
        String[] test = new String[]{"data1", "data2"};

        try {
            table.addData(test);
        } catch (CsvDataNotCorrectException e) {
            throw new RuntimeException(e);
        }

        assertEquals(new HashSet<>(Arrays.asList("data1", "data2")), new HashSet<>(table.getColumnData("column1")));
    }

    @Test
    public void testAddDataWithNullOrBlankColumnNames() {
        Table table = new BaseTable(new LinkedHashMap<>());
        assertThrows(CsvDataNotCorrectException.class, () -> table.addData(new String[]{"", "", ""}));
        assertThrows(CsvDataNotCorrectException.class, () -> table.addData(new String[]{null, null, null}));
    }

    @Test
    public void testAddDataWithEmptyColumns() {
        Table table = new BaseTable(new LinkedHashMap<>());
        try {
            table.addData(new String[]{"Column1", "Column2", "Column3"});
        } catch (CsvDataNotCorrectException e) {
            throw new RuntimeException(e);
        }

        assertEquals(3, table.getColumnNames().size());
    }

    @Test
    public void testAddDataWithInvalidDataSize() {
        Map<String, Column> columns = new LinkedHashMap<>();
        columns.put("Column1", new BaseColumn());
        columns.put("Column2", new BaseColumn());
        columns.put("Column3", new BaseColumn());

        Table table = new BaseTable(columns);

        assertThrows(CsvDataNotCorrectException.class,
                ()->table.addData(new String[]{"Value1", "Value2"}));
    }

    @Test
    public void testGetRowsCountEmptyColumns() {
        Table table = new BaseTable(new LinkedHashMap<>());
        assertEquals(0, table.getRowsCount());

        Map<String, Column> columns = new LinkedHashMap<>();
        columns.put("column1", new BaseColumn());
        columns.put("column2", new BaseColumn());
        table = new BaseTable(columns);
        assertEquals(1, table.getRowsCount());
    }
}
