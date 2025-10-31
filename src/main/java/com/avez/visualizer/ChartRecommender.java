// our default fallback when nothing happens is bar chart

package com.avez.visualizer;

import com.avez.visualizer.model.ChartRecommendation;
import com.avez.visualizer.model.ChartType;
import com.avez.visualizer.model.ColumnInfo;
import com.avez.visualizer.model.DataType;

import java.util.ArrayList;
import java.util.List;


// Recommends appropriate chart types based on column data type
public class ChartRecommender {

    // Recommends chart types for the single column
    public List<ChartRecommendation> recommendCharts(ColumnInfo columnInfo) {
        List<ChartRecommendation> recommendations = new ArrayList<>();

        DataType dataType = columnInfo.getDataType();
        int uniqueValues = columnInfo.getUniqueValues();

        switch (dataType) {
            case CATEGORICAL:
                recommendations.addAll(recommendForCategorical(uniqueValues)); // recommendForCategorical() returns a  List<ChartRecommendation> of multiple
                break;                                                         // recommendations, not a single ChartRecommendation, hence addAll() method is used

            case NUMERIC:
                recommendations.addAll(recommendForNumeric(columnInfo));
                break;

            case DATE:
                recommendations.addAll(recommendForDate(columnInfo));
                break;

            case BOOLEAN:
                recommendations.addAll(recommendForBoolean());
                break;

            case TEXT:
                recommendations.addAll(recommendForText(uniqueValues));
                break;

            case UNKNOWN:
                recommendations.add(new ChartRecommendation(
                        ChartType.BAR_CHART, // Bar chart is works reasonably well for most things.
                        "Default fallback for unknown data type",
                        3,
                        "Data type could not be determined"));
                break;
        }

        return recommendations;
    }


    // Recommender charts for categorical data
    private List<ChartRecommendation> recommendForCategorical(int uniqueValues) {
        List<ChartRecommendation> recommendations = new ArrayList<>();

        if (uniqueValues <= 7) { // 7 is ideal for pie/donut charts, balance between readability and coverage

            recommendations.add(new ChartRecommendation(
                    ChartType.PIE_CHART,
                    "Perfect for showing distribution of " + uniqueValues + " categories",
                    1));

            recommendations.add(new ChartRecommendation(
                    ChartType.DONUT_CHART,
                    "Modern alternative to pie chart",
                    1));

            recommendations.add(new ChartRecommendation(
                    ChartType.BAR_CHART,
                    "Good for comparing categories",
                    2));

        } else if (uniqueValues <= 10) { // Using else if, instead of directly else makes the condition explicit and future proof
            recommendations.add(new ChartRecommendation(
                    ChartType.BAR_CHART,
                    "Best for comparing " + uniqueValues + " categories",
                    1));

            recommendations.add(new ChartRecommendation(
                    ChartType.HORIZONTAL_BAR,
                    "Alternative for easier label reading",
                    1));

            recommendations.add(new ChartRecommendation(
                    ChartType.PIE_CHART,
                    "Can show distribution, but may be crowded",
                    2,
                    "Chart may be crowded with " + uniqueValues + " categories. Consider grouping."));
        }

        return recommendations;
    }

    // Recommender charts for numeric data
    private List<ChartRecommendation> recommendForNumeric(ColumnInfo columnInfo) {
        List<ChartRecommendation> recommendations = new ArrayList<>();

        recommendations.add(new ChartRecommendation(
                ChartType.HISTOGRAM,
                "Shows distribution of numeric values",
                1));

        recommendations.add(new ChartRecommendation(
                ChartType.BOX_PLOT,
                "Shows statistical distribution (median, quartiles, outliers)",
                2));

        recommendations.add(new ChartRecommendation(
                ChartType.BAR_CHART,
                "Can show individual values or aggregated stats",
                2));

        return recommendations;
    }

    // Recommender charts for date/time data
    private List<ChartRecommendation> recommendForDate(ColumnInfo columnInfo) {
        List<ChartRecommendation> recommendations = new ArrayList<>();

        recommendations.add(new ChartRecommendation(
                ChartType.LINE_CHART,
                "Perfect for showing trends over time",
                1));

        recommendations.add(new ChartRecommendation(
                ChartType.BAR_CHART,
                "Good for comparing values across time periods",
                2));

        return recommendations;
    }

    
    // Recommender charts for boolean data
    private List<ChartRecommendation> recommendForBoolean() {
        List<ChartRecommendation> recommendations = new ArrayList<>();

        recommendations.add(new ChartRecommendation(
                ChartType.PIE_CHART,
                "Perfect for showing true/false or yes/no distribution",
                1));

        recommendations.add(new ChartRecommendation(
                ChartType.DONUT_CHART,
                "Modern alternative for binary data",
                1));

        recommendations.add(new ChartRecommendation(
                ChartType.BAR_CHART,
                "Simple comparison of two values",
                2));

        return recommendations;
    }


    // Recommender charts for text data
    private List<ChartRecommendation> recommendForText(int uniqueValues) {
        List<ChartRecommendation> recommendations = new ArrayList<>();

        if (uniqueValues <= 20) {
            // Can show top values
            recommendations.add(new ChartRecommendation(
                    ChartType.BAR_CHART,
                    "Can show frequency of top values",
                    2,
                    "Consider showing only top 10 values for readability"));
        } else {
            // Too many to visualize
            recommendations.add(new ChartRecommendation(
                    ChartType.BAR_CHART,
                    "Limited visualization options for free-form text",
                    3,
                    "Text data with " + uniqueValues + " unique values. Consider filtering to top values."));
        }

        return recommendations;
    }
}
