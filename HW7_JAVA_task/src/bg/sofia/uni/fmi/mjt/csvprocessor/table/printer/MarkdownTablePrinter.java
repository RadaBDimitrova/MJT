package bg.sofia.uni.fmi.mjt.csvprocessor.table.printer;

import bg.sofia.uni.fmi.mjt.csvprocessor.table.Table;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MarkdownTablePrinter implements TablePrinter {
    static final int MIN_LENGTH = 3;

    @Override
    public Collection<String> printTable(Table table, ColumnAlignment... alignments) {
        List<String> result = new LinkedList<>();
        Map<String, Integer> columnMaxLengths = getColumnMaxLengths(table);
        List<String> headers = new LinkedList<>();
        List<String> alignment = new LinkedList<>();

        int j = 0;
        for (String columnName : table.getColumnNames()) {
            if (j < alignments.length) {
                headers.add(formatCell(columnName, columnMaxLengths.get(columnName), alignments[j]));
                alignment.add(getAlignmentIndicator(alignments[j++], columnMaxLengths.get(columnName)));
            } else {
                headers.add(formatCell(columnName, columnMaxLengths.get(columnName), ColumnAlignment.NOALIGNMENT));
                alignment.add(getAlignmentIndicator(ColumnAlignment.NOALIGNMENT, columnMaxLengths.get(columnName)));
            }
        }
        result.add("| " + String.join(" | ", headers) + " |");
        result.add("| " + String.join(" | ", alignment) + " |");
        for (int i = 0; i < table.getRowsCount() - 1; i++) {
            List<String> rowData = new LinkedList<>();
            for (String columnName : table.getColumnNames()) {
                List<String> columnData = new LinkedList<>(table.getColumnData(columnName));
                rowData.add(formatCell(columnData.get(i), columnMaxLengths.get(columnName),
                        getAlignment(alignments, columnName)));
            }
            result.add("| " + String.join(" | ", rowData) + " |");
        }
        return result;
    }

    private Map<String, Integer> getColumnMaxLengths(Table table) {
        Map<String, Integer> columnMaxLengths = new LinkedHashMap<>();

        for (String columnName : table.getColumnNames()) {
            int maxLength = columnName.length();

            for (String cell : table.getColumnData(columnName)) {
                maxLength = Math.max(maxLength, cell.length());
            }
            columnMaxLengths.put(columnName, Math.max(maxLength, MIN_LENGTH));
        }

        return columnMaxLengths;
    }

    private String getAlignmentIndicator(ColumnAlignment alignment, int maxLength) {
        String r = "-".repeat(Math.max(MIN_LENGTH - 1, maxLength - 1));
        return switch (alignment) {
            case CENTER -> {
                r = "-".repeat(Math.max(MIN_LENGTH - 2, maxLength - 2));
                yield ":" + r + ":";
            }
            case RIGHT -> r + ":";
            case LEFT -> ":" + r;
            case NOALIGNMENT -> "-".repeat(Math.max(MIN_LENGTH, maxLength));
        };
    }

    private String formatCell(String content, int maxLength, ColumnAlignment alignment) {
        if (maxLength == content.length()) {
            return content;
        }
        int padding = maxLength - content.length();
        return switch (alignment) {
            case CENTER:
                int leftPadding = padding / 2;
                int rightPadding = padding - leftPadding;
                yield " ".repeat(leftPadding) + content + " ".repeat(rightPadding);
            case LEFT:
                yield content + " ".repeat(padding);
            case RIGHT:
                yield " ".repeat(padding) + content;
            case NOALIGNMENT:
                yield content + " ".repeat(padding);
        };
    }

    private ColumnAlignment getAlignment(ColumnAlignment[] alignments, String columnName) {
        for (ColumnAlignment alignment : alignments) {
            if (alignment.name().equalsIgnoreCase(columnName)) {
                return alignment;
            }
        }
        return ColumnAlignment.NOALIGNMENT;
    }
}