package com.xingray.javafx.candleview;

import com.xingray.java.base.range.DoubleRange;
import com.xingray.java.collection.series.DoubleSeries;
import com.xingray.java.util.collection.CollectionUtil;
import com.xingray.java.view.Canvas;
import com.xingray.java.view.Color;
import com.xingray.java.view.Paint;
import com.xingray.javafx.view.FxColor;
import com.xingray.javafx.view.FxView;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class IndicatorView extends FxView {
    private Color backgroundLineColor = Color.rgb(50, 50, 50, 50);
    private Color textColor = Color.rgb(100, 50, 50, 50);
    private int textWidth = 40;
    private final List<Line> lines = new ArrayList();
    private DoubleSeries barSeries;
    private double halfCandleWidth;
    private double heightRatio;
    private boolean isDataUpdated;
    private DoubleRange valueRange;
    private double barWidth;
    private int size;
    private List<IndicatorLineCallback> indicatorLineCallbacks;

    public IndicatorView() {
        this.setOnMouseMoved(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                double x = event.getX();
                if (!(x > IndicatorView.this.getWidth() - (double) IndicatorView.this.textWidth)) {
                    double y = event.getY();
                    if (IndicatorView.this.size != 0 && IndicatorView.this.barWidth != 0.0) {
                        int index = (int) (x / IndicatorView.this.barWidth);
                        if (index < IndicatorView.this.size) {
                            if (IndicatorView.this.indicatorLineCallbacks != null && !IndicatorView.this.indicatorLineCallbacks.isEmpty()) {
                                Iterator var7 = IndicatorView.this.indicatorLineCallbacks.iterator();

                                while (var7.hasNext()) {
                                    IndicatorLineCallback callback = (IndicatorLineCallback) var7.next();
                                    callback.onSelect(IndicatorView.this.lines, index);
                                }
                            }

                        }
                    }
                }
            }
        });
    }

    public void addIndicatorLineCallback(IndicatorLineCallback callback) {
        if (this.indicatorLineCallbacks == null) {
            this.indicatorLineCallbacks = new LinkedList();
        }

        this.indicatorLineCallbacks.add(callback);
    }

    public void clear() {
        this.lines.clear();
        this.barSeries = null;
        this.invalidate();
    }

    public void addAll(List<Line> lines) {
        if (lines != null) {
            boolean changed = this.lines.addAll(lines);
            if (changed) {
                this.isDataUpdated = true;
                this.invalidate();
            }

        }
    }

    public void add(Line line) {
        this.lines.add(line);
        this.isDataUpdated = true;
        this.invalidate();
    }

    public void setLines(List<Line> lines) {
        this.lines.clear();
        if (lines != null) {
            this.lines.addAll(lines);
        }

        this.isDataUpdated = true;
        this.invalidate();
    }

    public void addLine(Line line) {
        this.lines.add(line);
        this.isDataUpdated = true;
        this.invalidate();
    }

    public void setBarSeriesList(DoubleSeries barSeries) {
        this.barSeries = barSeries;
        this.isDataUpdated = true;
        this.invalidate();
    }

    public void setBackgroundLineColor(Color backgroundLineColor) {
        this.backgroundLineColor = backgroundLineColor;
        this.invalidate();
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        this.invalidate();
    }

    public void setTextWidth(int textWidth) {
        this.textWidth = textWidth;
        this.invalidate();
    }

    public void onDraw(Canvas canvas) {
        if (this.isDataUpdated) {
            List<DoubleSeries> series = new ArrayList(this.lines);
            series.add(this.barSeries);
            this.valueRange = CollectionUtil.getRangeOfDoubleSeriesList(series);
            this.isDataUpdated = false;
        }

        this.size = 0;
        if (!CollectionUtil.isEmpty(this.barSeries)) {
            this.size = this.barSeries.length();
        } else if (!CollectionUtil.isEmpty(this.lines)) {
            this.size = ((Line) this.lines.get(0)).length();
        }

        if (this.size != 0) {
            double width = this.getWidth();
            double height = this.getHeight();
            this.halfCandleWidth = (width - (double) this.textWidth) / (double) (2 * (this.size + 1));
            double gap = this.halfCandleWidth * 0.2;
            this.halfCandleWidth *= 0.9;
            double min = this.valueRange.getStart();
            double max = this.valueRange.getEnd();
            if (max != min) {
                this.heightRatio = height * 0.9 / (max - min);
            } else {
                this.heightRatio = height * 0.9;
            }

            this.barWidth = (width - (double) this.textWidth) / (double) this.size;
            double[] xPositions = ViewHelper.getPositions(this.size, this.barWidth);
            this.drawBackgroundLines(canvas, width, height, min, max, xPositions);
            this.drawBars(canvas, this.barSeries, xPositions);
            this.drawLines(canvas, this.lines, xPositions);
        }
    }

    public void drawBackgroundLines(Canvas canvas, double width, double height, double min, double max, double[] positions) {
        double range = CandleUtil.getSplitRange(min, max, 8);
        double startLineValue = (double) ((int) (min / range)) * range;
        Paint paint = canvas.getPaint();
        paint.setDrawColor(this.backgroundLineColor);
        paint.setTextColor(this.textColor);

        int i;
        for (i = 0; i < 12; ++i) {
            double lineValue = startLineValue + (double) i * range;
            if (lineValue > max) {
                break;
            }

            double lineY = this.getY(lineValue);
            canvas.drawLine(0.0, lineY, width, lineY);
            canvas.drawText(Double.toString(lineValue), width - (double) this.textWidth, lineY);
        }

        i = 0;

        for (int size = positions.length; i < size; ++i) {
            if (i % 5 == 0) {
                double position = positions[i];
                canvas.drawLine(position, 0.0, position, height);
            }
        }

    }

    private void drawBars(Canvas canvas, DoubleSeries values, double[] xPositions) {
        if (!CollectionUtil.isEmpty(values)) {
            int i = 0;

            for (int size = values.length(); i < size; ++i) {
                double value = values.get(i);
                double position = xPositions[i];
                double top = Math.max(value, 0.0);
                double bottom = Math.min(value, 0.0);
                Color color;
                if (value > 0.0) {
                    color = FxColor.toColor(javafx.scene.paint.Color.RED);
                } else if (value < 0.0) {
                    color = FxColor.toColor(javafx.scene.paint.Color.GREEN);
                } else {
                    color = FxColor.toColor(javafx.scene.paint.Color.GRAY);
                }

                double topY = this.getY(top);
                canvas.getPaint().setFillColor(color);
                canvas.fillRect(position - this.halfCandleWidth, topY, this.halfCandleWidth * 2.0, (top - bottom) * this.heightRatio);
            }

        }
    }

    public void drawLines(Canvas canvas, List<Line> lines, double[] xPositions) {
        for (Line line : lines) {
            ViewHelper.drawLine(canvas, xPositions, line, 0, this::getY);
        }
    }

    private double getY(double v) {
        return 0.95 * this.getHeight() - (v - this.valueRange.getStart()) * this.heightRatio;
    }

    public interface IndicatorLineCallback {
        void onSelect(List<Line> var1, int var2);
    }
}