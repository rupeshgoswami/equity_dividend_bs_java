package com.quant.equitybs.core;

/**
 * Binomial Tree Pricer for Validation
 *
 * Used to validate Black-Scholes prices.
 * Document requires: price difference < 0.1%
 *
 * How it works:
 * 1. Split time T into N small steps
 * 2. At each step stock goes UP or DOWN
 * 3. Calculate option value at expiry
 * 4. Work backwards to get today's price
 *
 * Parameters:
 * u = up factor   = e^(sigma * sqrt(dt))
 * d = down factor = 1/u
 * p = probability of up move
 *   = (e^(r*dt) - d) / (u - d)
 */
public class BinomialTree {

    private final double spot;
    private final double strike;
    private final double T;
    private final double rate;
    private final double vol;
    private final int steps;

    public BinomialTree(double spot, double strike,
                         double T, double rate,
                         double vol, int steps) {
        this.spot   = spot;
        this.strike = strike;
        this.T      = T;
        this.rate   = rate;
        this.vol    = vol;
        this.steps  = steps;
    }

    // ─────────────────────────────────────────────────────
    // EUROPEAN CALL using Binomial Tree
    // ─────────────────────────────────────────────────────
    public double priceEuropeanCall() {

        double dt       = T / steps;
        double u        = Math.exp(vol * Math.sqrt(dt));
        double d        = 1.0 / u;
        double p        = (Math.exp(rate * dt) - d) / (u - d);
        double discount = Math.exp(-rate * dt);

        // Stock prices at expiry
        double[] stockPrices = new double[steps + 1];
        for (int i = 0; i <= steps; i++) {
            stockPrices[i] = spot
                           * Math.pow(u, i)
                           * Math.pow(d, steps - i);
        }

        // Option payoffs at expiry
        // Call payoff = max(S - K, 0)
        double[] optionValues = new double[steps + 1];
        for (int i = 0; i <= steps; i++) {
            optionValues[i] = Math.max(stockPrices[i] - strike, 0.0);
        }

        // Work backwards through tree
        for (int step = steps - 1; step >= 0; step--) {
            for (int i = 0; i <= step; i++) {
                optionValues[i] = discount
                    * (p * optionValues[i + 1]
                    + (1 - p) * optionValues[i]);
            }
        }

        return optionValues[0];
    }

    // ─────────────────────────────────────────────────────
    // AMERICAN CALL using Binomial Tree
    // ─────────────────────────────────────────────────────
    public double priceAmericanCall() {

        double dt       = T / steps;
        double u        = Math.exp(vol * Math.sqrt(dt));
        double d        = 1.0 / u;
        double p        = (Math.exp(rate * dt) - d) / (u - d);
        double discount = Math.exp(-rate * dt);

        double[] stockPrices = new double[steps + 1];
        for (int i = 0; i <= steps; i++) {
            stockPrices[i] = spot
                           * Math.pow(u, i)
                           * Math.pow(d, steps - i);
        }

        double[] optionValues = new double[steps + 1];
        for (int i = 0; i <= steps; i++) {
            optionValues[i] = Math.max(stockPrices[i] - strike, 0.0);
        }

        // Work backwards with early exercise check
        for (int step = steps - 1; step >= 0; step--) {
            for (int i = 0; i <= step; i++) {

                double stockAtNode = spot
                    * Math.pow(u, i)
                    * Math.pow(d, step - i);

                double holdValue = discount
                    * (p * optionValues[i + 1]
                    + (1 - p) * optionValues[i]);

                double exerciseValue = Math.max(
                    stockAtNode - strike, 0.0);

                optionValues[i] = Math.max(holdValue, exerciseValue);
            }
        }

        return optionValues[0];
    }

    // ─────────────────────────────────────────────────────
    // VALIDATE: Compare BS price vs Binomial Tree
    // Document requires difference < 0.1%
    // ─────────────────────────────────────────────────────
    public static ValidationResult validate(
            double bsPrice, double treePrice) {

        double difference  = Math.abs(bsPrice - treePrice);
        double percentDiff = (difference / bsPrice) * 100;
        boolean passed     = percentDiff < 0.1;

        return new ValidationResult(
            bsPrice, treePrice,
            difference, percentDiff, passed
        );
    }

    // ─────────────────────────────────────────────────────
    // Result holder for validation
    // Uses simple ASCII characters instead of Unicode
    // to avoid Windows terminal display issues
    // ─────────────────────────────────────────────────────
    public static class ValidationResult {

        public final double bsPrice;
        public final double treePrice;
        public final double difference;
        public final double percentDiff;
        public final boolean passed;

        public ValidationResult(double bsPrice, double treePrice,
                                 double difference, double percentDiff,
                                 boolean passed) {
            this.bsPrice     = bsPrice;
            this.treePrice   = treePrice;
            this.difference  = difference;
            this.percentDiff = percentDiff;
            this.passed      = passed;
        }

        @Override
        public String toString() {
            String status = passed ? "PASSED" : "FAILED";
            String line   = "+--------------------------------------+";

            return String.format(
                "%s%n"  +
                "|     Binomial Tree Validation         |%n" +
                "%s%n"  +
                "|  Black-Scholes Price :  %9.4f   |%n" +
                "|  Binomial Tree Price :  %9.4f   |%n" +
                "|  Difference          :  %9.4f   |%n" +
                "|  Percentage Diff     :  %8.4f%%  |%n" +
                "|  Validation          :  %-10s   |%n" +
                "%s",
                line,
                line,
                bsPrice,
                treePrice,
                difference,
                percentDiff,
                status,
                line
            );
        }
    }
}