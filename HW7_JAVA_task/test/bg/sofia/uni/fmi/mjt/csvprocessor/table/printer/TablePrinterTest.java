package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.BaseTable;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.Column;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment.CENTER;
import static bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment.LEFT;
import static bg.sofia.uni.fmi.mjt.csvprocessor.table.printer.ColumnAlignment.RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TablePrinterTest {
    @Test
    public void testPrintTableWithAlignedColumns() {
        Map<String, Column> columns = new LinkedHashMap<>();
        columns.put("column1", new BaseColumn(new LinkedHashSet<>(Arrays.asList("val1", "val2"))));
        columns.put("column2", new BaseColumn(new LinkedHashSet<>(Arrays.asList("val3", "val4"))));
        ColumnAlignment[] alignments = {LEFT, LEFT};
        Table table = new BaseTable(columns);
        Collection<String> printedTable = new MarkdownTablePrinter().printTable(table, alignments);

        List<String> expectedTable = Arrays.asList(
                "| column1 | column2 |",
                "| :------ | :------ |",
                "| val1    | val3    |",
                "| val2    | val4    |"
        );
        assertEquals(expectedTable, new ArrayList<>(printedTable));
    }

    @Test
    public void testPrintTableWithAlignedColumns2() {
        Map<String, Column> columns = new LinkedHashMap<>();
        columns.put("hdr", new BaseColumn(new LinkedHashSet<>(List.of("testcolumn"))));
        columns.put("testheader", new BaseColumn(new LinkedHashSet<>(List.of("b"))));
        columns.put("z", new BaseColumn(new LinkedHashSet<>(List.of("c"))));
        ColumnAlignment[] alignments = {RIGHT, LEFT, CENTER};
        Table table = new BaseTable(columns);
        Collection<String> printedTable = new MarkdownTablePrinter().printTable(table, alignments);

        List<String> expectedTable = Arrays.asList(
                "|        hdr | testheader |  z  |",
                "| ---------: | :--------- | :-: |",
                "| testcolumn | b          | c   |"

        );
        assertEquals(expectedTable, new ArrayList<>(printedTable));
    }

    @Test
    public void testPrintTableWithAlignedColumnsMissingAlignments() {
        Map<String, Column> columns = new LinkedHashMap<>();
        columns.put("hdr", new BaseColumn(new LinkedHashSet<>(List.of("testcolumn"))));
        columns.put("testheader", new BaseColumn(new LinkedHashSet<>(List.of("b"))));
        columns.put("z", new BaseColumn(new LinkedHashSet<>(List.of("c"))));
        ColumnAlignment[] alignments = {RIGHT, LEFT };
        Table table = new BaseTable(columns);
        Collection<String> printedTable = new MarkdownTablePrinter().printTable(table, alignments);

        List<String> expectedTable = Arrays.asList(
                "|        hdr | testheader | z   |",
                "| ---------: | :--------- | --- |",
                "| testcolumn | b          | c   |"

        );
        assertEquals(expectedTable, new ArrayList<>(printedTable));
    }
}
