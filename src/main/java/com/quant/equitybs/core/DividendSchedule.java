package com.quant.equitybs.core;

import java.util.TreeMap;
import java.util.Map;

/**
 * Stores dividend payments by ex-date
 * TreeMap keeps dates sorted automatically
 *
 * Example:
 * addDividend(0.5, 2.0) means
 * $2.0 dividend paid at 6 months (0.5 years)
 */
public class DividendSchedule {

    // Key = ex-date in years, Value = dividend amount in $
    private final TreeMap<Double, Double> dividends = new TreeMap<>();

    /**
     * Add a dividend payment
     * @param exDate  time in years e.g. 0.5 = 6 months
     * @param amount  dividend amount in dollars e.g. 2.0 = $2
     */
    public void addDividend(double exDate, double amount) {
        dividends.put(exDate, amount);
    }

    /**
     * Calculate Present Value of all dividends before time T
     * PV = sum of (amount * discountFactor(exDate))
     */
    public double presentValue(double T, DiscountCurve curve) {
        double totalPV = 0.0;

        for (Map.Entry<Double, Double> entry : dividends.entrySet()) {
            double exDate = entry.getKey();
            double amount = entry.getValue();

            // Only count dividends that happen before expiry T
            if (exDate <= T) {
                totalPV += amount * curve.getDiscountFactor(exDate);
            }
        }
        return totalPV;
    }

    /**
     * Check if any dividends exist before time T
     */
    public boolean hasDividendsBefore(double T) {
        return dividends.floorKey(T) != null;
    }

    public TreeMap<Double, Double> getDividends() {
        return dividends;
    }
}