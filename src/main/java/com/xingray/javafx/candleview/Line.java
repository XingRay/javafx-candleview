package com.xingray.javafx.candleview;


import com.xingray.java.collection.series.DoubleSeries;
import com.xingray.java.view.Color;

public interface Line extends DoubleSeries {
    Color getColor();

    String getName();
}
