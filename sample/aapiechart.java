package com.example.visualizer;

import java.io.*;
import java.util.Scanner;


import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;     // for saving PNGs
import org.jfree.data.general.DefaultPieDataset;




public class piechart 
{
    public static void main(String[] args) throws Exception
    {
        //DefaultPieDataset() creates a new, empty data container that is specifically designed to hold the data for a pie chart.
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("PlayStation", 100);
        dataset.setValue("Nintendo", 260);
        dataset.setValue("Xbox", 78);
        dataset.setValue("PC", 2);

        JFreeChart chart = ChartFactory.createPieChart("Gaming", dataset);
        JFreeChart chart1 = ChartFactory.createRingChart("Gaming", dataset, false, false, false);
          
        
      //  Scanner sc = new Scanner(System.in);
        int width = 784;
        int height = 698;

        File pieChart = new File("/Users/avisahai/datas/Projects/data0/src/main/java/com/example/visualizer/Piechart.png");
        ChartUtils.saveChartAsPNG(pieChart, chart, width, height);

        File pieChart1 = new File( "/Users/avisahai/datas/Projects/data0/src/main/java/com/example/visualizer/Piechart1.png");
        ChartUtils.saveChartAsPNG(pieChart1, chart, width, height);


    }
}
