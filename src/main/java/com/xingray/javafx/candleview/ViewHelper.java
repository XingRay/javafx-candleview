package com.xingray.javafx.candleview;


import com.xingray.java.base.interfaces.DoubleMapper;
import com.xingray.java.view.Canvas;

public class ViewHelper {
    public ViewHelper() {
    }

    public static void drawLine(Canvas canvas, double[] xPositions, Line line, int firstBarIndex, DoubleMapper<Double> mapper) {
        int k = 0;
        int size = line.length() - firstBarIndex;
        double[] yValues = new double[size];
        double[] xValues = new double[size];

        for(int i = 0; i < size; ++i) {
            double value = line.get(i + firstBarIndex);
            if (!Double.isNaN(value)) {
                yValues[k] = mapper.map(value);
                xValues[k] = xPositions[i];
                ++k;
            }
        }

        canvas.getPaint().setDrawColor(line.getColor());
        canvas.drawPolyline(xValues, yValues, k);
    }

    public static double[] getPositions(int pointCount, double distance) {
        double[] positions = new double[pointCount];

        for(int i = 0; i < pointCount; ++i) {
            positions[i] = ((double)i + 0.5) * distance;
        }

        return positions;
    }
}
