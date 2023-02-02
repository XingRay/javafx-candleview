package com.xingray.javafx.candleview;


import com.xingray.finanace.analysis.model.candle.Candle;
import com.xingray.finanace.analysis.model.data.DataList;
import com.xingray.java.base.range.DoubleRange;
import com.xingray.java.util.collection.CollectionUtil;

import java.util.List;

public class CandleUtil {
    public CandleUtil() {
    }

    public static double getSplitRange(double min, double max, int n) {
        if (!(min > max) && n >= 0) {
            double split = (max - min) / (double) (n * 10);
            return reserveValidNumber(split, 2) * 10.0;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static double reserveValidNumber(double v, int n) {
        if (v == 0.0) {
            return 0.0;
        } else {
            boolean minus = false;
            if (v < 0.0) {
                minus = true;
                v = -v;
            }

            double log10 = Math.log10(v);
            int ceil = (int) Math.ceil(log10);
            int floor = (int) Math.floor(log10);
            double value = 0.0;

            for (int i = 0; i < n; ++i) {
                double pow = Math.pow(10.0, floor - i);
                double v1 = (double) ((int) (v / pow)) * pow;
                value += v1;
                v -= v1;
            }

            return minus ? -value : value;
        }
    }

    public static DoubleRange getValuesRange(List<Candle> candleList) {
        if (candleList != null && candleList.size() != 0) {
            int length = candleList.size();
            Candle candle = candleList.get(0);
            double low = candle.getLow().doubleValue();
            double high = candle.getHigh().doubleValue();

            for (int i = 1; i < length; ++i) {
                Candle candle1 = candleList.get(i);
                double lowValue = candle1.getLow().doubleValue();
                if (lowValue < low) {
                    low = lowValue;
                }

                double highValue = candle1.getHigh().doubleValue();
                if (highValue > high) {
                    high = highValue;
                }
            }

            return new DoubleRange(low, high);
        } else {
            return null;
        }
    }

    public static DoubleRange getValuesRange(DataList<Candle> candleList) {
        if (candleList != null && candleList.length() != 0) {
            int length = candleList.length();
            Candle candle = (Candle) candleList.get(0);
            double low = candle.getLow().doubleValue();
            double high = candle.getHigh().doubleValue();

            for (int i = 1; i < length; ++i) {
                Candle candle1 = (Candle) candleList.get(i);
                double lowValue = candle1.getLow().doubleValue();
                if (lowValue < low) {
                    low = lowValue;
                }

                double highValue = candle1.getHigh().doubleValue();
                if (highValue > high) {
                    high = highValue;
                }
            }

            return new DoubleRange(low, high);
        } else {
            return null;
        }
    }

    public static DoubleRange getValuesRange(List<Line> lines, DoubleRange range) {
        if (!CollectionUtil.isEmpty(lines)) {
            for (Line line : lines) {
                int i = 0;

                for (int size = line.length(); i < size; ++i) {
                    double value = line.get(i);
                    if (value < range.getStart()) {
                        range.setStart(value);
                    }

                    if (value > range.getEnd()) {
                        range.setEnd(value);
                    }
                }
            }
        }

        return range;
    }

    public static DoubleRange getValuesRange(List<Candle> candleList, List<Line> lines) {
        DoubleRange range = getValuesRange(candleList);
        return getValuesRange(lines, range);
    }

    public static DoubleRange getValuesRange(DataList<Candle> candleList, List<Line> lines) {
        DoubleRange range = getValuesRange(candleList);
        return getValuesRange(lines, range);
    }
}
