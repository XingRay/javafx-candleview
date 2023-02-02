package com.xingray.javafx.candleview;


import com.xingray.finanace.analysis.model.candle.Candle;
import com.xingray.finanace.analysis.model.candle.CandleList;
import com.xingray.finanace.analysis.model.data.DataList;
import com.xingray.java.base.interfaces.EventHandler;
import com.xingray.java.base.range.DoubleRange;
import com.xingray.java.view.Canvas;
import com.xingray.java.view.Color;
import com.xingray.java.view.Paint;
import com.xingray.javafx.view.FxColor;
import com.xingray.javafx.view.FxView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CandleView extends FxView {
    private Color backgroundLineColor = Color.rgb(50, 50, 50, 50);
    private Color textColor = Color.rgb(100, 50, 50, 50);
    private int textWidth = 40;
    private double barWidthRatio = 0.9;
    private int barCountMin = 0;
    private int barCountMax = 1000;
    private Color upColor;
    private Color downColor;
    private Color noneColor;
    private final DataList<Candle> candleList;
    private final List<Line> lines;
    private double heightRatio;
    private DoubleRange valueRange;
    private boolean isDataUpdated;
    private double barWidth;
    private List<EventHandler<Candle>> selectEventHandlerList;
    private int firstBarIndex;

    public CandleView() {
        this.upColor = FxColor.toColor(javafx.scene.paint.Color.RED);
        this.downColor = FxColor.toColor(javafx.scene.paint.Color.GREEN);
        this.noneColor = FxColor.toColor(javafx.scene.paint.Color.GRAY);
        this.candleList = new CandleList();
        this.lines = new ArrayList<>();
        this.setOnMouseMoved(event -> {
            double x = event.getX();
            if (!(x > CandleView.this.getWidth() - (double) CandleView.this.textWidth)) {
                double y = event.getY();
                DataList<Candle> candleList = CandleView.this.candleList;
                if (candleList.length() != 0) {
                    int index = (int) (x / CandleView.this.barWidth) + CandleView.this.firstBarIndex;
                    if (index < candleList.length()) {
                        if (CandleView.this.selectEventHandlerList != null) {
                            for (EventHandler<Candle> candleEventHandler : CandleView.this.selectEventHandlerList) {
                                candleEventHandler.onEvent(candleList.get(index));
                            }
                        }
                    }
                }
            }
        });
    }

    public void addSelectEventHandler(EventHandler<Candle> eventHandler) {
        if (this.selectEventHandlerList == null) {
            this.selectEventHandlerList = new LinkedList();
        }

        this.selectEventHandlerList.add(eventHandler);
    }

    public void removeSelectEventHandler(EventHandler<Candle> eventHandler) {
        if (this.selectEventHandlerList != null) {
            this.selectEventHandlerList.remove(eventHandler);
        }
    }

    public void setBarWidthRatio(double barWidthRatio) {
        this.barWidthRatio = barWidthRatio;
    }

    public void setUpColor(Color upColor) {
        this.upColor = upColor;
    }

    public void setDownColor(Color downColor) {
        this.downColor = downColor;
    }

    public void setNoneColor(Color noneColor) {
        this.noneColor = noneColor;
    }

    public void setTextWidth(int textWidth) {
        this.textWidth = textWidth;
    }

    public void setBackgroundLineColor(Color color) {
        this.backgroundLineColor = color;
        this.invalidate();
    }

    public void setTextColor(Color color) {
        this.textColor = color;
        this.invalidate();
    }

    public void addLine(Line line) {
        this.lines.add(line);
        this.isDataUpdated = true;
        this.invalidate();
    }

    public void setLines(List<Line> lines) {
        this.lines.clear();
        this.lines.addAll(lines);
        this.isDataUpdated = true;
        this.invalidate();
    }

    public void setBarCountMin(int barCountMin) {
        this.barCountMin = barCountMin;
    }

    public void setBarCountMax(int barCountMax) {
        this.barCountMax = barCountMax;
    }

    public void clear() {
        if (this.candleList.length() != 0 || !this.lines.isEmpty()) {
            this.candleList.clear();
            this.lines.clear();
            this.isDataUpdated = true;
            this.invalidate();
        }
    }

    public void setCandleList(List<Candle> candleList) {
        this.candleList.clear();
        if (candleList != null) {
            this.candleList.addAll(candleList);
        }

        this.isDataUpdated = true;
        this.invalidate();
    }

    public void addAll(List<Candle> candleList) {
        if (candleList != null) {
            this.candleList.addAll(candleList);
            this.isDataUpdated = true;
            this.invalidate();
        }
    }

    public void add(Candle candle) {
        if (candle != null) {
            this.candleList.add(candle);
            this.isDataUpdated = true;
            this.invalidate();
        }
    }

    private double getY(double v) {
        return 0.95 * this.getHeight() - (v - this.valueRange.getStart()) * this.heightRatio;
    }

    public void onDraw(Canvas canvas) {
        DataList<Candle> candleList = this.candleList;
        int length = candleList.length();
        if (length != 0) {
            int barCount = Math.min(this.barCountMax, Math.max(this.barCountMin, length));
            if (barCount != 0) {
                this.firstBarIndex = Math.max(length - barCount, 0);
                double width = this.getWidth();
                double height = this.getHeight();
                if (this.isDataUpdated) {
                    this.valueRange = CandleUtil.getValuesRange(candleList, this.lines);
                }

                double min = this.valueRange.getStart();
                double max = this.valueRange.getEnd();
                if (max != min) {
                    this.heightRatio = height * 0.9 / (max - min);
                } else {
                    this.heightRatio = height * 0.9;
                }

                this.barWidth = (width - (double) this.textWidth) / (double) barCount;
                double halfCandleWidth = this.barWidth * this.barWidthRatio / 2.0;
                double[] xPositions = ViewHelper.getPositions(barCount, this.barWidth);
                this.drawBackgroundLines(canvas, width, height, min, max, xPositions);
                this.drawCandles(canvas, candleList, this.firstBarIndex, barCount, xPositions, halfCandleWidth);
                this.drawLines(canvas, this.lines, this.firstBarIndex, xPositions);
            }
        }
    }

    public void drawCandles(Canvas canvas, DataList<Candle> candleList, int firstBarIndex, int barCount, double[] xPositions, double halfCandleWidth) {
        if (candleList != null && candleList.length() != 0) {
            int size = Math.min(barCount, candleList.length() - firstBarIndex);

            for (int i = 0; i < size; ++i) {
                double position = xPositions[i];
                Candle candle = (Candle) candleList.get(i + firstBarIndex);
                this.drawCandle(canvas, position, candle, halfCandleWidth, this.heightRatio);
            }

        }
    }

    public void drawCandle(Canvas canvas, double position, Candle candle, double halfCandleWidth, double heightRatio) {
        double open = candle.getOpen().doubleValue();
        double close = candle.getClose().doubleValue();
        double high = candle.getHigh().doubleValue();
        double low = candle.getLow().doubleValue();
        double top = Math.max(open, close);
        double bottom = Math.min(open, close);
        Color color;
        if (close > open) {
            color = this.upColor;
        } else if (close < open) {
            color = this.downColor;
        } else {
            color = this.noneColor;
        }

        double topY = this.getY(top);
        double bottomY = this.getY(bottom);
        double highY = this.getY(high);
        double lowY = this.getY(low);
        if (high > top) {
            canvas.getPaint().setDrawColor(color);
            canvas.drawLine(position, highY, position, topY);
        }

        if (close > open) {
            canvas.getPaint().setDrawColor(color);
            canvas.drawRect(position - halfCandleWidth, topY, halfCandleWidth * 2.0, (top - bottom) * heightRatio);
        } else if (close < open) {
            canvas.getPaint().setFillColor(color);
            canvas.fillRect(position - halfCandleWidth, topY, halfCandleWidth * 2.0, (top - bottom) * heightRatio);
        } else {
            canvas.getPaint().setDrawColor(color);
            canvas.drawLine(position - halfCandleWidth, topY, position + halfCandleWidth, topY);
        }

        if (low < bottom) {
            canvas.getPaint().setDrawColor(color);
            canvas.drawLine(position, lowY, position, bottomY);
        }

    }

    public void drawLines(Canvas canvas, List<Line> lines, int firstBarIndex, double[] xPositions) {
        for (Line line : lines) {
            ViewHelper.drawLine(canvas, xPositions, line, firstBarIndex, this::getY);
        }
    }

    public void drawBackgroundLines(Canvas canvas, double width, double height, double min, double max, double[] positions) {
        double range = CandleUtil.getSplitRange(min, max, 10);
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
}
