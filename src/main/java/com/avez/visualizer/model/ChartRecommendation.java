package com.avez.visualizer.model;


// Represents a chart recommendation for a column
public class ChartRecommendation {
    private ChartType chartType;
    private String reason; // Why this chart is recommended
    private int priority; // 1 = best, 2 = good, 3 = acceptable
    private String warning; // Optional warning message

    public ChartRecommendation(ChartType chartType, String reason, int priority) {
        this.chartType = chartType;
        this.reason = reason;
        this.priority = priority;
        this.warning = null;
    }

    public ChartRecommendation(ChartType chartType, String reason, int priority, String warning) {
        this.chartType = chartType;
        this.reason = reason;
        this.priority = priority;
        this.warning = warning;
    }

    // Getters and Setters
    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public boolean hasWarning() {
        return warning != null && !warning.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(chartType).append(" (Priority: ").append(priority).append(")");
        sb.append("\n  Reason: ").append(reason);
        if (hasWarning()) {
            sb.append("\n  Warning: ").append(warning);
        }
        return sb.toString();
    }
}
