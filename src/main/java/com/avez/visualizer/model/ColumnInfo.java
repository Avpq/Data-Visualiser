package com.avez.visualizer.model;


// This class stores metadata about each column
public class ColumnInfo {
    private String columnName;
    private DataType dataType; // the type of data stored in the column, details in the DataType.java file
    private int totalValues;
    private int uniqueValues;
    private int nullCount;


    // the type of column cannot be decoded in the constructor, is handled in the CSVAnalyzer file
    public ColumnInfo(String columnName) {
        this.columnName = columnName;
        this.totalValues = 0;
        this.uniqueValues = 0;
        this.nullCount = 0;
    } 


    // Getters and Setters methods
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public int getTotalValues() {
        return totalValues;
    }

    public void setTotalValues(int totalValues) {
        this.totalValues = totalValues;
    }

    public int getUniqueValues() {
        return uniqueValues;
    }

    public void setUniqueValues(int uniqueValues) {
        this.uniqueValues = uniqueValues;
    }

    public int getNullCount() {
        return nullCount;
    }

    public void setNullCount(int nullCount) {
        this.nullCount = nullCount;
    }

    @Override
    public String toString() {
        return "Column: " + columnName +
                ", Type: " + dataType +
                ", Total: " + totalValues +
                ", Unique: " + uniqueValues +
                ", Nulls: " + nullCount;
    }
}
