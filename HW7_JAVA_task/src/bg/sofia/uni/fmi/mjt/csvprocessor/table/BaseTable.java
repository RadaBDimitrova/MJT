package bg.sofia.uni.fmi.mjt.csvprocessor.table;

import bg.sofia.uni.fmi.mjt.csvprocessor.exceptions.CsvDataNotCorrectException;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.BaseColumn;
import bg.sofia.uni.fmi.mjt.csvprocessor.table.column.Column;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseTable implements Table {
    Map<String, Column> columns;

    public BaseTable() {
        this.columns = new LinkedHashMap<>();
    }

    @Override
    public void addData(String[] data) throws CsvDataNotCorrectException {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null.");
        }

        if (columns.isEmpty()) {
            for (String columnName : data) {
                if (columnName == null || columnName.trim().isEmpty()) {
                    throw new CsvDataNotCorrectException("Column names cannot be null or empty.");
                }
                columns.put(columnName, new BaseColumn());
            }
        } else {
            if (data.length != columns.size()) {
                throw new CsvDataNotCorrectException("Invalid data size for the columns.");
            }

            int idx = 0;
            for (String columnName : columns.keySet()) {
                columns.get(columnName).addData(data[idx++]);
            }
        }
    }

    @Override
    public Collection<String> getColumnNames() {
        return columns.keySet();
    }

    @Override
    public Collection<String> getColumnData(String column) {
        if (column == null || column.trim().isEmpty() || !columns.containsKey(column)) {
            throw new IllegalArgumentException("Invalid column name.");
        }
        return columns.get(column).getData();
    }

    @Override
    public int getRowsCount() {
        if (columns.isEmpty()) {
            return 0;
        }
        return columns.values().iterator().next().getData().size();
    }
}
