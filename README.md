# CSV Visualizer

A powerful and user-friendly Java application for visualizing CSV data with intelligent chart recommendations. Transform your data into beautiful charts with just a few clicks or commands.

## Features

- **Smart Chart Recommendations**: Automatically analyzes your data and suggests the most appropriate chart types
- **Multiple Chart Types**: Supports Pie Charts, Bar Charts, Histograms, Line Charts, and more
- **Dual Interface**: Choose between CLI (Command Line Interface) or GUI (Graphical User Interface)
- **Data Analysis**: Automatically detects data types (numeric, categorical, date) and provides statistical insights
- **Cross-Platform**: Works on Windows, macOS, and Linux
- **Easy Export**: Save charts as high-quality PNG images

## Supported Chart Types

- **Pie Chart**: Perfect for showing proportions and percentages
- **Bar Chart**: Compare values across categories
- **Histogram**: Visualize distribution of numeric data
- **Line Chart**: Show trends over time or continuous data
- **Scatter Plot**: Display relationships between two variables (coming soon)

## Requirements

- **Java**: JDK 11 or higher
- **Maven**: 3.6+ (for building from source)
- **Operating System**: Windows, macOS, or Linux

## Installation

### Option 1: Download Installer (macOS)

1. Download the latest `CSV Visualizer-X.X.X.dmg` from releases
2. Open the DMG file
3. Drag the application to your Applications folder
4. Launch "CSV Visualizer"

### Option 2: Build from Source

```bash
# Clone the repository
git clone https://github.com/Avpq/Data-Visualiser.git
cd Data-Visualiser

# Build the project
mvn clean package

# Run the GUI application
java -jar target/visualizer-1.0-SNAPSHOT.jar

# Or run CLI mode directly
mvn exec:java
```

## Usage

### GUI Mode (Recommended)

Run the application by double-clicking the installed app or:

```bash
java -jar target/visualizer-1.0-SNAPSHOT.jar
```

The GUI provides an interactive terminal-like interface where you can:
1. Enter the path to your CSV file
2. View column analysis
3. Select columns to visualize
4. Choose from recommended chart types
5. Specify output location and filename

### CLI Mode

For terminal enthusiasts, run directly via Maven:

```bash
mvn exec:java
```

Follow the interactive prompts:
1. **Load CSV**: Enter the path to your CSV file
2. **Select Column**: Choose which column to visualize
3. **Choose Chart**: Pick from recommended chart types
4. **Save**: Specify where to save the generated chart

### Example Workflow

```
📊 CSV VISUALIZER TOOL 📊
Transform Your Data into Charts

STEP 1: LOAD CSV FILE
📁 Enter CSV file path: /path/to/your/data.csv

✅ CSV loaded successfully!
   📊 Rows: 1000
   📋 Columns: 5

STEP 2: SELECT COLUMN TO VISUALIZE
📊 Available Columns:

 1. Product Category    [CATEGORICAL] - 8 unique values
 2. Sales Amount        [NUMERIC]     - 945 unique values
 3. Date               [DATE]        - 365 unique values

🔢 Select column number: 1

STEP 3: SELECT CHART TYPE
📈 Recommended Charts:

1. Pie Chart (Priority: 1)
   💡 Great for showing distribution of categories

🔢 Select chart number: 1

STEP 4: SPECIFY OUTPUT LOCATION
💾 Save Chart Options:
1. Save in home directory (default)
2. Specify custom path

Select option (1 or 2): 1
📄 Enter filename: product_distribution

✅ Chart generated successfully!
📁 Saved as: /Users/username/product_distribution.png
```

## Building a Mac Installer

The project includes a script to create a native macOS DMG installer:

```bash
./build-installer.sh
```

This will:
1. Build the fat JAR with all dependencies
2. Create a native macOS application bundle
3. Package it as a DMG installer

**Requirements**: macOS with `jpackage` tool (included in JDK 14+)

## Project Structure

```
Data-Visualiser/
├── src/main/java/com/avez/visualizer/
│   ├── Main.java                 # Entry point for CLI mode
│   ├── GUIWrapper.java          # GUI application wrapper
│   ├── CLIInterface.java        # Command-line interface
│   ├── UIRenderer.java          # Unicode/ASCII rendering
│   ├── CSVReader.java           # CSV file parsing
│   ├── CSVAnalyzer.java         # Data analysis
│   ├── ChartRecommender.java    # Chart recommendation engine
│   ├── ChartGenerator.java      # Chart creation using JFreeChart
│   └── model/                   # Data models
│       ├── CSVData.java
│       ├── ColumnInfo.java
│       ├── ChartType.java
│       ├── ChartRecommendation.java
│       └── DataType.java
├── sample/                      # Sample CSV files
├── build-installer.sh          # macOS installer build script
└── pom.xml                     # Maven configuration
```

## Technologies Used

- **Java 11+**: Core programming language
- **JFreeChart 1.5.6**: Chart generation library
- **Apache Commons CSV 1.14.1**: CSV parsing
- **Maven**: Build and dependency management
- **Swing**: GUI framework

## How It Works

1. **CSV Parsing**: Reads and parses CSV files using Apache Commons CSV
2. **Data Analysis**: Analyzes each column to determine data type, unique values, and statistics
3. **Smart Recommendations**: Uses heuristics to recommend appropriate chart types based on:
   - Data type (numeric, categorical, date)
   - Number of unique values
   - Distribution patterns
4. **Chart Generation**: Creates publication-quality charts using JFreeChart
5. **Export**: Saves charts as PNG images with customizable dimensions

## Contributing

Contributions are welcome! Feel free to:
- Report bugs
- Suggest new features
- Submit pull requests

## License

This project is open source and available for educational and personal use.

## Author

Avez

## Acknowledgments

- JFreeChart for the excellent charting library
- Apache Commons for CSV parsing utilities
