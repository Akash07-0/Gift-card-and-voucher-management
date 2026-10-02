package com.example.voucher.dto;

public class TimeSeriesDataPoint {
    private String date;
    private double value;

    public TimeSeriesDataPoint() {}

    public TimeSeriesDataPoint(String date, double value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
