package com.xingray.javafx.candleview;


import com.xingray.java.view.Color;

import java.util.Arrays;

public class ValueLine implements Line {
    private double[] values;
    private Color color;
    private String name;

    public ValueLine(double[] values, Color color, String name) {
        this.values = values;
        this.color = color;
        this.name = name;
    }

    public double[] getValues() {
        return this.values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public double get(int index) {
        return this.values[index];
    }

    public int length() {
        return this.values.length;
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return "ValueLine{" +
                "values=" + Arrays.toString(values) +
                ", color=" + color +
                ", name='" + name + '\'' +
                '}';
    }
}