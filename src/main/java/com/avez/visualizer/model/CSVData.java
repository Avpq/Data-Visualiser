package com.avez.visualizer.model;

import org.apache.commons.csv.CSVRecord;
import java.util.List;


//Container class that holds both headers and records from a CSV file
public class CSVData {
    private List<String> headers; // name of the coulmn wala row
    private List<CSVRecord> records; // rest of the rows

    public CSVData(List<String> headers, List<CSVRecord> records) {
        this.headers = headers;
        this.records = records;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<CSVRecord> getRecords() {
        return records;
    }

    public int getRowCount() {
        return records.size();
    }

    public int getColumnCount() {
        return headers.size();
    }

    @Override
    public String toString() {
        return "CSVData{" +
                "columns=" + headers.size() +
                ", rows=" + records.size() +
                ", headers=" + headers +
                '}';
    }
}
