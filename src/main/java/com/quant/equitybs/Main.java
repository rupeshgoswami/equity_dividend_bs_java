package com.quant.equitybs;

import com.quant.equitybs.core.BlackScholesEngine;
import com.quant.equitybs.core.DiscountCurve;
import com.quant.equitybs.core.DividendSchedule;
import com.quant.equitybs.model.Greeks;
import com.quant.equitybs.pricer.AmericanPricer;

/**
 * Main Runner - Dividend Adjusted Black Scholes Engine
 *
 * This runs 3 scenarios:
 *
 * Scenario 1: European Call with Continuous Dividend Yield
 * Scenario 2: European Call with Discrete Cash Dividend
 * Scenario 3: American Call with Early Exercise Check
 *
 * Option Details used in all scenarios:
 * Stock Price  (S) = $100
 * Strike Price (K) = $105
 * Expiry       (T) = 1 year
 * Risk Free Rate   = 5%
 * Volatility       = 20%
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("===========================================");
        System.out.println("  Dividend-Adjusted Black-Scholes Engine  ");
        System.out.println("===========================================");
        System.out.println();

        // ── Option Parameters ───────────────────────────────
        double spot   = 100.0;  // Current stock price
        double strike = 105.0;  // Strike price
        double T      = 1.0;    // 1 year to expiry
        double rate   = 0.05;   // 5% risk free rate
        double vol    = 0.20;   // 20% volatility

        // Create the Black Scholes Engine
        BlackScholesEngine engine = new BlackScholesEngine(
            spot, strike, T, rate, vol
        );

        // Create a flat discount curve at 5%
        DiscountCurve curve = new DiscountCurve(rate);

        // ── SCENARIO 1: Continuous Dividend Yield ───────────
        System.out.println("-------------------------------------------");
        System.out.println(" SCENARIO 1: Continuous Dividend Yield (3%)");
        System.out.println("-------------------------------------------");

        double q = 0.03; // 3% continuous dividend yield
        double contPrice = engine.priceContinuousYield(q);

        System.out.printf("  Stock Price  : $%.2f%n",  spot);
        System.out.printf("  Strike Price : $%.2f%n",  strike);
        System.out.printf("  Expiry       :  %.1f year%n", T);
        System.out.printf("  Rate         :  %.0f%%%n", rate * 100);
        System.out.printf("  Volatility   :  %.0f%%%n", vol  * 100);
        System.out.printf("  Div Yield    :  %.0f%%%n", q    * 100);
        System.out.println();
        System.out.printf("  ✓ Call Price = $%.4f%n", contPrice);
        System.out.println();

        // ── SCENARIO 2: Discrete Cash Dividend ──────────────
        System.out.println("-------------------------------------------");
        System.out.println(" SCENARIO 2: Discrete Cash Dividend ($2 at 6 months)");
        System.out.println("-------------------------------------------");

        // Add a $2 dividend paid at 6 months (0.5 years)
        DividendSchedule divSchedule = new DividendSchedule();
        divSchedule.addDividend(0.5, 2.0);

        // Calculate PV of dividend
        double divPV = divSchedule.presentValue(T, curve);
        double discPrice = engine.priceDiscreteDividends(divSchedule, curve);

        System.out.printf("  Dividend     : $2.00 at 6 months%n");
        System.out.printf("  PV of Dividend: $%.4f%n", divPV);
        System.out.printf("  Adjusted Spot : $%.4f%n", spot - divPV);
        System.out.println();
        System.out.printf("  ✓ Call Price = $%.4f%n", discPrice);
        System.out.println();

        // ── SCENARIO 3: Greeks ───────────────────────────────
        System.out.println("-------------------------------------------");
        System.out.println(" SCENARIO 3: Option Greeks (q = 3%)");
        System.out.println("-------------------------------------------");

        Greeks greeks = engine.computeGreeks(q);

        System.out.printf("  Delta : %8.4f  (price change per $1 stock move)%n",
                           greeks.getDelta());
        System.out.printf("  Gamma : %8.4f  (delta change per $1 stock move)%n",
                           greeks.getGamma());
        System.out.printf("  Vega  : %8.4f  (price change per 1%% vol move)%n",
                           greeks.getVega());
        System.out.printf("  Theta : %8.4f  (price change per 1 day)%n",
                           greeks.getTheta());
        System.out.printf("  Rho   : %8.4f  (price change per 1%% rate move)%n",
                           greeks.getRho());
        System.out.println();

        // ── SCENARIO 4: American Option ──────────────────────
        System.out.println("-------------------------------------------");
        System.out.println(" SCENARIO 4: American Call vs European Call");
        System.out.println("-------------------------------------------");

        // Add a larger dividend to trigger early exercise
        DividendSchedule americanDivs = new DividendSchedule();
        americanDivs.addDividend(0.5, 3.0); // $3 dividend at 6 months

        AmericanPricer americanPricer = new AmericanPricer(
            engine, curve, americanDivs
        );

        americanPricer.printComparison();

        System.out.println();
        System.out.println("===========================================");
        System.out.println("           All Scenarios Complete!         ");
        System.out.println("===========================================");
    }
}