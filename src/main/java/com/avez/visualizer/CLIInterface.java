package com.avez.visualizer;

import com.avez.visualizer.model.CSVData;
import com.avez.visualizer.model.ColumnInfo;
import com.avez.visualizer.model.ChartRecommendation;
import com.avez.visualizer.model.ChartType;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class CLIInterface {

    private Scanner scanner;
    private CSVReader csvReader;
    private CSVAnalyzer csvAnalyzer;
    private ChartRecommender chartRecommender;
    private ChartGenerator chartGenerator;

    private CSVData currentData;
    private List<ColumnInfo> columnInfos;

    public CLIInterface() {
        this.scanner = new Scanner(System.in);
        this.csvReader = new CSVReader();
        this.csvAnalyzer = new CSVAnalyzer();
        this.chartRecommender = new ChartRecommender();
        this.chartGenerator = new ChartGenerator();
    }


    // main() method to start the CLI
    public void start() {
        printWelcome();

        boolean running = true;
        while (running) {
            try {
                // Step 1: Load CSV
                if (!loadCSV()) {
                    continue;
                }

                // Step 2: Analyze columns
                analyzeColumns();

                // Step 3: Let user select column and generate chart
                boolean continueGenerating = true;
                while (continueGenerating) {
                    if (selectColumnAndGenerateChart()) {
                        System.out.print("\n🔄 Generate another chart? (y/n): ");
                        String response = scanner.nextLine().trim().toLowerCase();
                        continueGenerating = response.equals("y") || response.equals("yes");
                    } else {
                        continueGenerating = false;
                    }
                }

                // Ask if user wants to load another CSV
                System.out.print("\n📁 Load another CSV file? (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                running = response.equals("y") || response.equals("yes");

            } catch (Exception e) {
                System.err.println("\n❌ Error: " + e.getMessage());
                System.out.print("\nTry again? (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                running = response.equals("y") || response.equals("yes");
            }
        }

        System.out.println("\n👋 Thank you for using CSV Visualizer!");
        scanner.close();
    }

    /**
     * Prints welcome message
     */
    private void printWelcome() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║      📊 CSV VISUALIZER TOOL 📊        ║");
        System.out.println("║   Transform Your Data into Charts     ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * Loads a CSV file
     */
    private boolean loadCSV() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("STEP 1: LOAD CSV FILE");
        System.out.println("=".repeat(50));

        System.out.print("📁 Enter CSV file path (or 'exit' to quit): ");
        String filePath = scanner.nextLine().trim();

        if (filePath.equalsIgnoreCase("exit")) {
            return false;
        }

        if (filePath.isEmpty()) {
            System.out.println("❌ File path cannot be empty!");
            return false;
        }

        try {
            System.out.println("\n⏳ Loading CSV file...");
            currentData = csvReader.readCSV(filePath);

            System.out.println("✅ CSV loaded successfully!");
            System.out.println("   📊 Rows: " + currentData.getRowCount());
            System.out.println("   📋 Columns: " + currentData.getColumnCount());

            return true;

        } catch (IOException e) {
            System.err.println("❌ Error loading CSV: " + e.getMessage());
            System.out.println("   💡 Make sure the file path is correct and the file exists.");
            return false;
        }
    }

    /**
     * Analyzes the loaded CSV columns
     */
    private void analyzeColumns() {
        System.out.println("\n⏳ Analyzing columns...");
        columnInfos = csvAnalyzer.analyzeColumns(currentData);
        System.out.println("✅ Analysis complete!");
    }

    /**
     * Lets user select a column and generate a chart
     */
    private boolean selectColumnAndGenerateChart() {
        // Display available columns
        System.out.println("\n" + "=".repeat(50));
        System.out.println("STEP 2: SELECT COLUMN TO VISUALIZE");
        System.out.println("=".repeat(50));

        System.out.println("\n📊 Available Columns:\n");
        for (int i = 0; i < columnInfos.size(); i++) {
            ColumnInfo info = columnInfos.get(i);
            System.out.printf("%2d. %-25s [%s] - %d unique values%n",
                    i + 1,
                    info.getColumnName(),
                    info.getDataType(),
                    info.getUniqueValues());
        }

        // Get user selection
        System.out.print("\n🔢 Select column number (or 0 to go back): ");
        String input = scanner.nextLine().trim();

        int columnIndex;
        try {
            columnIndex = Integer.parseInt(input) - 1;
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input! Please enter a number.");
            return false;
        }

        if (columnIndex == -1) {
            return false; // User chose to go back
        }

        if (columnIndex < 0 || columnIndex >= columnInfos.size()) {
            System.out.println("❌ Invalid column number!");
            return false;
        }

        ColumnInfo selectedColumn = columnInfos.get(columnIndex);

        // Show column details
        System.out.println("\n✅ You selected: " + selectedColumn.getColumnName());
        System.out.println("   Type: " + selectedColumn.getDataType());
        System.out.println("   Total values: " + selectedColumn.getTotalValues());
        System.out.println("   Unique values: " + selectedColumn.getUniqueValues());
        System.out.println("   Null values: " + selectedColumn.getNullCount());

        // Get chart recommendations
        List<ChartRecommendation> recommendations = chartRecommender.recommendCharts(selectedColumn);

        if (recommendations.isEmpty()) {
            System.out.println("❌ No chart recommendations available for this column.");
            return false;
        }

        // Display recommendations
        System.out.println("\n" + "=".repeat(50));
        System.out.println("STEP 3: SELECT CHART TYPE");
        System.out.println("=".repeat(50));

        System.out.println("\n📈 Recommended Charts:\n");
        for (int i = 0; i < recommendations.size(); i++) {
            ChartRecommendation rec = recommendations.get(i);
            System.out.printf("%d. %s (Priority: %d)%n",
                    i + 1,
                    formatChartTypeName(rec.getChartType()),
                    rec.getPriority());
            System.out.println("   💡 " + rec.getReason());
            if (rec.hasWarning()) {
                System.out.println("   ⚠️  " + rec.getWarning());
            }
            System.out.println();
        }

        // Get chart selection
        System.out.print("🔢 Select chart number: ");
        input = scanner.nextLine().trim();

        int chartIndex;
        try {
            chartIndex = Integer.parseInt(input) - 1;
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input! Please enter a number.");
            return false;
        }

        if (chartIndex < 0 || chartIndex >= recommendations.size()) {
            System.out.println("❌ Invalid chart number!");
            return false;
        }

        ChartRecommendation selectedChart = recommendations.get(chartIndex);

        // NEW: Get output file path and name
        System.out.println("\n" + "=".repeat(50));
        System.out.println("STEP 4: SPECIFY OUTPUT LOCATION");
        System.out.println("=".repeat(50));

        System.out.println("\n💾 Save Chart Options:");
        System.out.println("1. Save in current directory (default)");
        System.out.println("2. Specify custom path");

        System.out.print("\nChoose option (1 or 2): ");
        String saveOption = scanner.nextLine().trim();

        String outputPath;

        if (saveOption.equals("2")) {
            // Custom path
            System.out.print("\n📁 Enter directory path: ");
            System.out.println("   Examples:");
            System.out.println("   - Windows: C:/Users/YourName/Desktop/");
            System.out.println("   - Mac/Linux: /home/username/Documents/");
            System.out.print("\n   Path: ");
            String directoryPath = scanner.nextLine().trim();

            if (directoryPath.isEmpty()) {
                System.out.println("⚠️  Empty path provided. Using current directory.");
                directoryPath = "";
            } else {
                // Ensure path ends with separator
                if (!directoryPath.endsWith("/") && !directoryPath.endsWith("\\")) {
                    directoryPath += "/";
                }

                // Validate directory exists
                java.io.File dir = new java.io.File(directoryPath);
                if (!dir.exists() || !dir.isDirectory()) {
                    System.out.println("⚠️  Directory does not exist. Using current directory.");
                    directoryPath = "";
                }
            }

            System.out.print("\n📄 Enter filename (without .png extension): ");
            String filename = scanner.nextLine().trim();

            if (filename.isEmpty()) {
                // Generate default filename
                filename = selectedColumn.getColumnName().toLowerCase()
                        .replace(" ", "_") + "_" +
                        selectedChart.getChartType().toString().toLowerCase();
            }

            // Remove .png if user added it
            if (filename.toLowerCase().endsWith(".png")) {
                filename = filename.substring(0, filename.length() - 4);
            }

            outputPath = directoryPath + filename + ".png";

        } else {
            // Default: current directory
            System.out.print("\n📄 Enter filename (press Enter for default): ");
            String filename = scanner.nextLine().trim();

            if (filename.isEmpty()) {
                // Generate default filename
                filename = selectedColumn.getColumnName().toLowerCase()
                        .replace(" ", "_") + "_" +
                        selectedChart.getChartType().toString().toLowerCase();
            }

            // Remove .png if user added it
            if (filename.toLowerCase().endsWith(".png")) {
                filename = filename.substring(0, filename.length() - 4);
            }

            outputPath = filename + ".png";
        }

        System.out.println("\n📍 Chart will be saved as: " + outputPath);
        System.out.print("Proceed? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("y") && !confirm.equals("yes")) {
            System.out.println("❌ Chart generation cancelled.");
            return false;
        }

        // Generate the chart
        try {
            System.out.println("\n🎨 Generating chart...");

            // Special handling for scatter plot (needs two columns)
            if (selectedChart.getChartType() == ChartType.SCATTER_PLOT) {
                System.out.println("⚠️  Scatter plot requires two numeric columns.");
                System.out.println("This feature is not yet implemented in CLI.");
                System.out.println("Please select a different chart type.");
                return false;
            }

            chartGenerator.generateChart(
                    selectedColumn.getColumnName(),
                    currentData.getRecords(),
                    selectedChart.getChartType(),
                    outputPath);

            System.out.println("✅ Chart generated successfully!");
            System.out.println("📁 Saved as: " + outputPath);

            return true;

        } catch (IOException e) {
            System.err.println("❌ Error generating chart: " + e.getMessage());
            return false;
        } catch (UnsupportedOperationException e) {
            System.err.println("❌ " + e.getMessage());
            return false;
        }
    }

    /**
     * Formats chart type name for display
     */
    private String formatChartTypeName(ChartType chartType) {
        String name = chartType.toString().replace("_", " ");
        // Capitalize first letter of each word
        String[] words = name.split(" ");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                formatted.append(Character.toUpperCase(word.charAt(0)));
                formatted.append(word.substring(1).toLowerCase());
                formatted.append(" ");
            }
        }
        return formatted.toString().trim();
    }
}
