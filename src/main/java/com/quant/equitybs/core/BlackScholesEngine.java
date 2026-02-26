package com.quant.equitybs.core;

import com.quant.equitybs.model.Greeks;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Black-Scholes Pricing Engine with Dividend Support
 *
 * Supports two dividend models:
 * 1. Continuous Dividend Yield  (Merton 1973)
 * 2. Discrete Cash Dividends    (Forward Adjustment Method)
 *
 * Parameters:
 * spot   = current stock price        e.g. 100.0
 * strike = option strike price        e.g. 105.0
 * T      = time to expiry in years    e.g. 1.0
 * rate   = risk free interest rate    e.g. 0.05 = 5%
 * vol    = volatility (sigma)         e.g. 0.20 = 20%
 */
public class BlackScholesEngine {

    private final double spot;
    private final double strike;
    private final double T;
    private final double rate;
    private final double vol;

    // Apache Commons Math normal distribution
    // Used for N(d1) and N(d2) calculations
    private static final NormalDistribution NORM = new NormalDistribution();

    public BlackScholesEngine(double spot, double strike,
                               double T, double rate, double vol) {
        this.spot   = spot;
        this.strike = strike;
        this.T      = T;
        this.rate   = rate;
        this.vol    = vol;
    }

    // ─────────────────────────────────────────────────────────
    // METHOD 1: Continuous Dividend Yield (Merton 1973)
    //
    // Formula:
    // d1 = [ ln(S/K) + (r - q + σ²/2) * T ] / (σ * √T)
    // d2 = d1 - σ * √T
    //
    // Call = S * e^(-qT) * N(d1) - K * e^(-rT) * N(d2)
    //
    // where:
    // q    = continuous dividend yield e.g. 0.03 = 3%
    // N()  = cumulative normal distribution
    // ─────────────────────────────────────────────────────────
    public double priceContinuousYield(double q) {

        double sqrtT = Math.sqrt(T);

        double d1 = (Math.log(spot / strike)
                    + (rate - q + 0.5 * vol * vol) * T)
                    / (vol * sqrtT);

        double d2 = d1 - vol * sqrtT;

        double call = spot  * Math.exp(-q    * T) * NORM.cumulativeProbability(d1)
                    - strike * Math.exp(-rate * T) * NORM.cumulativeProbability(d2);

        return call;
    }

    // ─────────────────────────────────────────────────────────
    // METHOD 2: Discrete Cash Dividends (Forward Adjustment)
    //
    // Step 1: Calculate PV of all dividends before expiry
    //         divPV = sum of (dividend * e^(-r * exDate))
    //
    // Step 2: Adjust spot price
    //         S_adj = S - divPV
    //
    // Step 3: Run standard Black-Scholes on S_adj
    //
    // Example:
    // S=100, $2 dividend at 6 months
    // divPV = 2 * e^(-0.05 * 0.5) = 1.9506
    // S_adj = 100 - 1.9506 = 98.05
    // ─────────────────────────────────────────────────────────
    public double priceDiscreteDividends(DividendSchedule divs,
                                          DiscountCurve curve) {

        // Step 1: Get present value of all dividends
        double divPV = divs.presentValue(T, curve);

        // Step 2: Adjust the spot price
        double sAdj = spot - divPV;

        // Safety check
        if (sAdj <= 0) {
            throw new IllegalStateException(
                "Dividend PV (" + divPV + ") exceeds spot price (" + spot + ")"
            );
        }

        // Step 3: Standard Black-Scholes on adjusted spot
        double sqrtT = Math.sqrt(T);

        double d1 = (Math.log(sAdj / strike)
                    + (rate + 0.5 * vol * vol) * T)
                    / (vol * sqrtT);

        double d2 = d1 - vol * sqrtT;

        double call = sAdj   * NORM.cumulativeProbability(d1)
                    - strike * Math.exp(-rate * T)
                             * NORM.cumulativeProbability(d2);

        return call;
    }

    // ─────────────────────────────────────────────────────────
    // METHOD 3: Compute All Greeks (Continuous Yield)
    //
    // Delta = dV/dS    how much price changes per $1 move in stock
    // Gamma = d²V/dS²  how much delta changes per $1 move in stock
    // Vega  = dV/dσ    how much price changes per 1% move in vol
    // Theta = dV/dt    how much price changes per 1 day passing
    // Rho   = dV/dr    how much price changes per 1% move in rates
    // ─────────────────────────────────────────────────────────
    public Greeks computeGreeks(double q) {

        double sqrtT = Math.sqrt(T);

        double d1 = (Math.log(spot / strike)
                    + (rate - q + 0.5 * vol * vol) * T)
                    / (vol * sqrtT);

        double d2 = d1 - vol * sqrtT;

        // N(d1) and N(d2) = cumulative normal probabilities
        double Nd1 = NORM.cumulativeProbability(d1);
        double Nd2 = NORM.cumulativeProbability(d2);

        // n(d1) = normal probability density at d1
        double nd1 = NORM.density(d1);

        // Calculate each Greek
        double delta = Math.exp(-q * T) * Nd1;

        double gamma = Math.exp(-q * T) * nd1
                     / (spot * vol * sqrtT);

        double vega  = spot * Math.exp(-q * T) * nd1 * sqrtT;

        double theta = (-spot * Math.exp(-q * T) * nd1 * vol / (2 * sqrtT))
                     - (rate  * strike * Math.exp(-rate * T) * Nd2)
                     + (q     * spot   * Math.exp(-q    * T) * Nd1);

        double rho   = strike * T * Math.exp(-rate * T) * Nd2;

        return new Greeks(delta, gamma, vega, theta, rho);
    }

    // Getters
    public double getSpot()   { return spot;   }
    public double getStrike() { return strike; }
    public double getT()      { return T;      }
    public double getRate()   { return rate;   }
    public double getVol()    { return vol;    }
}