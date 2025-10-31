package com.avez.visualizer;

import com.avez.visualizer.model.CSVData; // CSVData container for header and records of the CSV file
import com.avez.visualizer.model.ColumnInfo;
import com.avez.visualizer.model.DataType;
import org.apache.commons.csv.CSVRecord; // CSVRecord represents one row of data

import java.time.LocalDate; // represents a date
import java.time.format.DateTimeFormatter; // defines how to parse date strings 
import java.time.format.DateTimeParseException;
import java.util.*;



// Analyzes CSV data to determine column types and characteristics
public class CSVAnalyzer {

    // Common date formats to try when detecting dates
    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"));


    // ColumnInfo store metadata about one column
    // this method is used to analyze the datatype of all the columns of the CSV
    public List<ColumnInfo> analyzeColumns(CSVData csvData) {
        List<ColumnInfo> columnInfoList = new ArrayList<>(); // Creates an empty list that will store ColumnInfo objects, one columnInfo object represents 1 column
        List<String> headers = csvData.getHeaders();
        List<CSVRecord> records = csvData.getRecords(); // gets all the data rows from the columns ie from below header till end


        // Analyze each column
        for (String header : headers) {
            ColumnInfo columnInfo = analyzeEachCoumn(header, records);
            columnInfoList.add(columnInfo);
        }

        return columnInfoList;
    }


    // Analyzes a single column to determine its charecteristics totalValues, uniqueVales, nullValues and  ALSO the datatype
    private ColumnInfo analyzeEachCoumn(String columnName, List<CSVRecord> records) {
        ColumnInfo singleColumnInfo = new ColumnInfo(columnName); // creates the ColumnInfo object with the with the specified column name

        Set<String> uniqueValues = new HashSet<>(); // stores the unique values in the column
        int numericCount = 0;
        int dateCount = 0;
        int booleanCount = 0;
        int nullCount = 0;
        int totalValues = 0;
        int decimalCount = 0; //Tracks how many numeric values have decimals


        // Examine each value in this column
        for (CSVRecord record : records) {
            String value = record.get(columnName); // Gets the value from this row for the column we're analyzing
            totalValues++;
            /*
             * totalValues represents the dataset size, while validValues(used aage) represents usable data. totalValues info is necessary as it helps to determine the 
             * total number of ROWS and so help in the percentage calculation. to determine the actual data How many rows have actual data we use the
             * validValues parameter
             */

            // Check for null/empty
            if (value == null || value.trim().isEmpty()) {
                nullCount++;
                continue;
            }

            value = value.trim();
            uniqueValues.add(value);

            if (isNumeric(value)) {
                numericCount++;

                // Check if this number has a decimal point, IDs (postal codes, phone numbers) never have decimals
                // Real quantities (prices, measurements) often do
                if (value.contains(".")) {
                    decimalCount++;
                }
            }

            if (isDate(value))
                dateCount++;

            if (isBoolean(value))
                booleanCount++;
        }

        singleColumnInfo.setTotalValues(totalValues);
        singleColumnInfo.setUniqueValues(uniqueValues.size());
        singleColumnInfo.setNullCount(nullCount);

        // now we determine the datatype of the column
        int validValues = totalValues - nullCount; // non-empty values in the column
        DataType dataType = determineDataType(
                numericCount,
                dateCount,
                booleanCount,
                uniqueValues.size(),
                validValues,
                decimalCount); 

        singleColumnInfo.setDataType(dataType); // now the singleColumnInfo object hold complete info about the specified column
        return singleColumnInfo;
    }

    // analyses datatype of the column
    private DataType determineDataType(int numericCount, int dateCount,
            int booleanCount, int uniqueCount,
            int validValues, int decimalCount) { 
        if (validValues == 0) {
            return DataType.UNKNOWN;
        }

        double numericPercent = (double) numericCount / validValues;
        double datePercent = (double) dateCount / validValues;
        double booleanPercent = (double) booleanCount / validValues;

        // Check dates and booleans first (they take priority)
        if (datePercent >= 0.8) // 80% is a practical balance between accuracy and tolerance for noisy or imperfect data.
            return DataType.DATE;

        if (booleanPercent >= 0.8)
            return DataType.BOOLEAN;

        // Smart numeric detection - distinguish IDs from real numbers
        if (numericPercent >= 0.8) {
            double uniquePercent = (double) uniqueCount / validValues;
            double decimalPercent = numericCount > 0 ? (double) decimalCount / numericCount : 0;

            /*
             * Detect numeric IDs (postal codes, phone numbers, product IDs):
             * - High uniqueness (90%+): Most values are different
             * - Large dataset (100+ rows): Enough data to be confident
             * - Few decimals (<10%): IDs are integers, not decimals
             * 
             * Examples:
             * - Postal Code: 751001, 751002, ... (unique, no decimals) → TEXT
             * - Phone Number: 9876543210, 9876543211, ... (unique, no decimals) → TEXT
             * - Price: 999.99, 25.50, 75.00 (has decimals) → NUMERIC
             * - Quantity: 5, 10, 8 (low uniqueness, small dataset) → NUMERIC
             */
            if (uniquePercent >= 0.9 && validValues >= 100 && decimalPercent < 0.1) {
                return DataType.TEXT; // It's an ID, not a real number
            }

            return DataType.NUMERIC; // It's a real number for calculations/charts
        }

        // If few unique values (less than 20% of total), it's categorical
        double uniquePercent = (double) uniqueCount / validValues;
        if (uniquePercent < 0.2 && uniqueCount <= 10) // uniqueCount <= 10, ensures there aren't too many categories
            return DataType.CATEGORICAL;

        // Otherwise, it's free-form text
        return DataType.TEXT;
    }

    private boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true; // Conversion succeeded
        } catch (NumberFormatException e) {
            return false; // Conversion failed
        }
    }

    private boolean isDate(String value) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate.parse(value, formatter);
                return true;
            } catch (DateTimeParseException e) {
                // Try the next formatter
            }
        }
        return false;
    }

    private boolean isBoolean(String value) {
        String lower = value.toLowerCase();
        return lower.equals("true") || lower.equals("false") ||
                lower.equals("yes") || lower.equals("no") ||
                lower.equals("1") || lower.equals("0") ||
                lower.equals("t") || lower.equals("f");
    }
}