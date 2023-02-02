package com.xingray.javafx.candleview;


import com.xingray.finanace.analysis.indicator.Indicator;
import com.xingray.finanace.analysis.model.candle.Candle;
import com.xingray.finance.analysis.number.Num;
import com.xingray.java.view.Color;

public class IndicatorLine implements Line {
    private String name;
    private Indicator<Num, Candle> indicator;
    private Color color;

    public IndicatorLine(Indicator<Num, Candle> indicator, Color color) {
        this("", indicator, color);
    }

    public IndicatorLine(String name, Indicator<Num, Candle> indicator, Color color) {
        this.name = name;
        this.indicator = indicator;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public double get(int index) {
        return ((Num)this.indicator.get(index)).doubleValue();
    }

    public int length() {
        return this.indicator.length();
    }

    public Color getColor() {
        return this.color;
    }
}
