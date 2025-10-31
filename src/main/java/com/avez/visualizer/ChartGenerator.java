package com.avez.visualizer;

import com.avez.visualizer.model.ChartType;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class ChartGenerator {

    private static final int CHART_WIDTH = 800;
    private static final int CHART_HEIGHT = 600;

    /**
     * Generates a chart wrt a single column
     * 
     * @param columnName Name of the column to visualize
     * @param records    All CSV records
     * @param chartType  Type of chart to generate
     * @param outputPath Where to save the chart image
     * @throws IOException If chart cannot be saved
     */

    public void generateChart(String columnName, List<CSVRecord> records, ChartType chartType, String outputPath) throws IOException {

        JFreeChart chart;

        switch (chartType) {
            case PIE_CHART:
                chart = createPieChart(columnName, records);
                break;

            case BAR_CHART:
                chart = createBarChart(columnName, records);
                break;

            case HORIZONTAL_BAR:
                chart = createHorizontalBarChart(columnName, records);
                break;

            case LINE_CHART:
                chart = createLineChart(columnName, records);
                break;

            case HISTOGRAM:
                chart = createHistogram(columnName, records);
                break;

            case SCATTER_PLOT:
                // Scatter plot requires TWO columns, so handled it separately
                throw new UnsupportedOperationException(
                        "Scatter plot requires two columns. Use generateScatterPlot() instead.");

            case BOX_PLOT:
                chart = createBoxPlot(columnName, records);
                break;

            case DONUT_CHART:
                chart = createDonutChart(columnName, records);
                break;

            default:
                // Fallback to bar chart, it is decent for most cases
                chart = createBarChart(columnName, records);
                break;
        }

        saveChart(chart, outputPath);
    }


    public void generateScatterPlot(String xColumnName, String yColumnName, List<CSVRecord> records, String outputPath) throws IOException {
        JFreeChart chart = createScatterPlot(xColumnName, yColumnName, records);
        saveChart(chart, outputPath);
    }


    // pie chart, JFreeChart returns a chart object (from JFreeChart library)
    private JFreeChart createPieChart(String columnName, List<CSVRecord> records) {
        Map<String, Integer> valueFrequency = countValueFrequency(columnName, records);

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Integer> entry : valueFrequency.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        return ChartFactory.createPieChart( // ChartFactory is a factory class from JFreeChart library that creates charts
                columnName + " Distribution",
                dataset,
                true, // Shows a legend (key) that explains what each color represents
                true, // Tooltips, When you hover over a slice with your mouse, it shows detailed info, would not make sense when saving as a png, but when viewing in GUI fayada hoga
                false // URLs, Whether chart elements are clickable links (for web-based charts)
        );
    }


    // donut chart
    private JFreeChart createDonutChart(String columnName, List<CSVRecord> records) {
        Map<String, Integer> valueFrequency = countValueFrequency(columnName, records);

        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map.Entry<String, Integer> entry : valueFrequency.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        return ChartFactory.createRingChart(
                columnName + " Distribution",
                dataset,
                true, // Legend
                true, // Tooltips
                false // URLs
        );
    }


    // vertical bar chart 
    private JFreeChart createBarChart(String columnName, List<CSVRecord> records) {
        Map<String, Integer> valueFrequency = countValueFrequency(columnName, records);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : valueFrequency.entrySet()) {
            dataset.addValue(entry.getValue(), "Frequency", entry.getKey());
        }

        return ChartFactory.createBarChart(
                columnName + " Frequency",
                columnName,
                "Count",
                dataset,
                PlotOrientation.VERTICAL,
                false, // Legend
                true, // Tooltips
                false // URLs
        );
    }


    // horizontal bar chart 
    private JFreeChart createHorizontalBarChart(String columnName, List<CSVRecord> records) {
        Map<String, Integer> valueFrequency = countValueFrequency(columnName, records);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : valueFrequency.entrySet()) {
            dataset.addValue(entry.getValue(), "Frequency", entry.getKey());
        }

        return ChartFactory.createBarChart(
                columnName + " Frequency",
                "Count", // X-axis (swapped)
                columnName, // Y-axis (swapped)
                dataset,
                PlotOrientation.HORIZONTAL, // Horizontal orientation
                false,
                true,
                false);
    }


    // line chart
    private JFreeChart createLineChart(String columnName, List<CSVRecord> records) {
        Map<String, Integer> valueFrequency = countValueFrequency(columnName, records);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : valueFrequency.entrySet()) {
            dataset.addValue(entry.getValue(), "Frequency", entry.getKey());
        }

        return ChartFactory.createLineChart(
                columnName + " Trend",
                columnName,
                "Count",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
    }


    // Creates a histogram showing distribution of numeric values, groups continuous numeric data into bins/ranges
    private JFreeChart createHistogram(String columnName, List<CSVRecord> records) {
        // Extract numeric values from the column
        List<Double> numericValues = extractNumericValues(columnName, records);

        if (numericValues.isEmpty()) {
            throw new IllegalArgumentException(
                    "No numeric values found in column: " + columnName);
        }

        // Convert list to array for histogram
        double[] values = numericValues.stream().mapToDouble(Double::doubleValue).toArray();

        // Create histogram dataset with 10 bins
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries(columnName, values, 10);

        return ChartFactory.createHistogram(
                columnName + " Distribution",
                columnName,
                "Frequency",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
    }


    // // Creates a box plot showing statistical distribution Shows: min, Q1, median, Q3, max, and outliers
    // private JFreeChart createBoxPlot(String columnName, List<CSVRecord> records) {
    //     List<Double> numericValues = extractNumericValues(columnName, records);

    //     if (numericValues.isEmpty()) {
    //         throw new IllegalArgumentException(
    //                 "No numeric values found in column: " + columnName);
    //     }

    //     // Create box and whisker dataset
    //     DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
    //     dataset.add(numericValues, "Series", columnName);

    //     return ChartFactory.createBoxAndWhiskerChart(
    //             columnName + " Distribution",
    //             columnName,
    //             "Value",
    //             dataset,
    //             false // Legend
    //     );
    // }
    
    private JFreeChart createBoxPlot(String columnName, List<CSVRecord> records) {
        List<Double> numericValues = extractNumericValues(columnName, records);

        if (numericValues.isEmpty()) {
            throw new IllegalArgumentException(
                    "No numeric values found in column: " + columnName);
        }

        // Create box and whisker dataset
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        dataset.add(numericValues, "Series", columnName);

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(
                columnName + " Distribution",
                columnName,
                "Value",
                dataset,
                false // Legend
        );

        // Improve box plot appearance
        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        org.jfree.chart.renderer.category.BoxAndWhiskerRenderer renderer = (org.jfree.chart.renderer.category.BoxAndWhiskerRenderer) plot
                .getRenderer();

        // Make it look cleaner
        renderer.setFillBox(true); // Fill the box
        renderer.setMeanVisible(false); // Hide the mean marker (reduces clutter)
        renderer.setMaximumBarWidth(0.10); // Make box narrower

        return chart;
    }


    /**
     * Creates a scatter plot showing correlation between two numeric columns
     * 
     * @param xColumnName Column for X-axis
     * @param yColumnName Column for Y-axis
     */
    private JFreeChart createScatterPlot(String xColumnName, String yColumnName, List<CSVRecord> records) {
        XYSeries series = new XYSeries("Data Points");

        // Extract paired numeric values
        for (CSVRecord record : records) {
            String xValue = record.get(xColumnName);
            String yValue = record.get(yColumnName);

            if (xValue == null || yValue == null ||
                    xValue.trim().isEmpty() || yValue.trim().isEmpty()) {
                continue;
            }

            try {
                double x = Double.parseDouble(xValue.trim());
                double y = Double.parseDouble(yValue.trim());
                series.add(x, y);
            } catch (NumberFormatException e) {
                // Skip non-numeric values
                continue;
            }
        }

        if (series.isEmpty()) {
            throw new IllegalArgumentException(
                    "No valid numeric pairs found for columns: " + xColumnName + ", " + yColumnName);
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);

        return ChartFactory.createScatterPlot(
                xColumnName + " vs " + yColumnName,
                xColumnName,
                yColumnName,
                dataset,
                PlotOrientation.VERTICAL,
                false, // Legend
                true, // Tooltips
                false // URLs
        );
    }


    // Helper: Counts frequency of each value in a column
    private Map<String, Integer> countValueFrequency(String columnName, List<CSVRecord> records) {
        Map<String, Integer> frequency = new HashMap<>();

        for (CSVRecord record : records) {
            String value = record.get(columnName);

            if (value == null || value.trim().isEmpty()) {
                continue;
            }

            value = value.trim();
            frequency.put(value, frequency.getOrDefault(value, 0) + 1);
        }

        return frequency;
    }


    // Helper: Extracts numeric values from a column
    private List<Double> extractNumericValues(String columnName, List<CSVRecord> records) {
        List<Double> values = new ArrayList<>();

        for (CSVRecord record : records) {
            String value = record.get(columnName);

            if (value == null || value.trim().isEmpty()) {
                continue;
            }

            try {
                double numericValue = Double.parseDouble(value.trim());
                values.add(numericValue);
            } catch (NumberFormatException e) {
                // Skip non-numeric values
            }
        }

        return values;
    }


    // Saves a chart as a PNG file
    private void saveChart(JFreeChart chart, String outputPath) throws IOException {
        File outputFile = new File(outputPath);
        ChartUtils.saveChartAsPNG(outputFile, chart, CHART_WIDTH, CHART_HEIGHT);
        System.out.println("✅ Chart saved to: " + outputFile.getAbsolutePath());
    }
}
