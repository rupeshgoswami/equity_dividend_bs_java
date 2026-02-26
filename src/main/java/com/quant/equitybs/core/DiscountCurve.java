package com.quant.equitybs.core;

/**
 * Flat discount curve using continuous compounding
 * Formula: DF(t) = e^(-rate * t)
 *
 * Example: rate=5%, t=1 year
 * DF = e^(-0.05 * 1) = 0.9512
 * Meaning: $1 received in 1 year = $0.9512 today
 */
public class DiscountCurve {

    private final double rate; // annual interest rate e.g. 0.05 = 5%

    public DiscountCurve(double rate) {
        this.rate = rate;
    }

    /**
     * Returns discount factor for time t (in years)
     */
    public double getDiscountFactor(double t) {
        return Math.exp(-rate * t);
    }

    public double getRate() {
        return rate;
    }
}