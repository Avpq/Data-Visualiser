package com.avez.visualizer.model;


// This enum defines what types of data a column can contain
public enum DataType {
    NUMERIC, 
    CATEGORICAL, // Text with limited unique values (like payment methods for a store data csv)
    DATE, 
    TEXT, // Free-form text (like descriptions, names)
    BOOLEAN, 
    UNKNOWN // Can't determine type
}
