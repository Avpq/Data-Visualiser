package com.avez.visualizer;

import com.avez.visualizer.model.CSVData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class CSVReader {

    // Reads a CSV file and returns ALL records
    public CSVData readCSV(String filePath) throws IOException {
        File file = new File(filePath);

        // all the parsing rules
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader() // Auto-detect headers from first row
                .setSkipHeaderRecord(true) // Don't include header row in records
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .setDelimiter(',')
                .get();

        
        // Create parser and read the file
        CSVParser parser = CSVParser.parse(file, StandardCharsets.UTF_8, format);

        List<String> headers = new ArrayList<>(parser.getHeaderMap().keySet()); // Headers were already read when parser was created, just extracting them to this LIST
        List<CSVRecord> records = parser.getRecords(); // Reads the rest (esentially the data rows) from the CSV and stores them in the LIST recrods 

        parser.close();

        // Return both headers and records together
        return new CSVData(headers, records);
    }
}
