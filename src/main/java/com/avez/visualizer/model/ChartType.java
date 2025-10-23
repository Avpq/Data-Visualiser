package com.avez.visualizer.model;

//Defines the types of charts that can be generated
public enum ChartType {
    PIE_CHART, // For categorical data (≤7 categories)
    BAR_CHART, // For categorical data or comparisons
    HORIZONTAL_BAR, // Alternative to bar chart
    LINE_CHART, // For time series or trends
    HISTOGRAM, // For numeric distribution
    SCATTER_PLOT, // For correlation between two numeric columns
    BOX_PLOT, // For numeric data distribution
    DONUT_CHART // Alternative to pie chart
}
