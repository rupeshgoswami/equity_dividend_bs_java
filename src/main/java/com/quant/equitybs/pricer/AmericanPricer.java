package com.quant.equitybs.pricer;

import com.quant.equitybs.core.BlackScholesEngine;
import com.quant.equitybs.core.DiscountCurve;
import com.quant.equitybs.core.DividendSchedule;

/**
 * American Option Pricer with Dividend Support
 *
 * American options can be exercised ANY time before expiry.
 * This is different from European options which can only
 * be exercised AT expiry.
 *
 * Key Rule for Early Exercise:
 * It is optimal to exercise an American CALL early
 * just before a dividend ex-date IF:
 *
 *   Dividend Amount > r * K * (T - exDate)
 *
 * Meaning: The dividend you receive by exercising early
 * is greater than the interest you lose on the strike price.
 *
 * Example:
 * Dividend = $3, r = 5%, K = 100, T - exDate = 0.5 years
 * Interest cost = 0.05 * 100 * 0.5 = $2.50
 * Since $3 > $2.50 → Early exercise IS optimal
 */
public class AmericanPricer {

    private final BlackScholesEngine engine;
    private final DiscountCurve curve;
    private final DividendSchedule divSchedule;

    public AmericanPricer(BlackScholesEngine engine,
                           DiscountCurve curve,
                           DividendSchedule divSchedule) {
        this.engine      = engine;
        this.curve       = curve;
        this.divSchedule = divSchedule;
    }

    // ─────────────────────────────────────────────────────────
    // MAIN METHOD: Price American Call with Dividends
    //
    // Total Price = European Price + Early Exercise Premium
    //
    // Step 1: Get European price using discrete dividend method
    // Step 2: Check if early exercise is optimal
    // Step 3: Add early exercise premium if applicable
    // ─────────────────────────────────────────────────────────
    public double priceAmericanCall() {

        // Step 1: European price as base
        double europeanPrice = engine.priceDiscreteDividends(
                                   divSchedule, curve);

        // Step 2 & 3: Add early exercise premium
        double earlyPremium = computeEarlyExercisePremium();

        double americanPrice = europeanPrice + earlyPremium;

        return americanPrice;
    }

    // ─────────────────────────────────────────────────────────
    // Early Exercise Premium Calculation
    //
    // For each dividend before expiry, check:
    // IF dividend > r * K * (T - exDate)
    // THEN early exercise is optimal → add premium
    // ─────────────────────────────────────────────────────────
    private double computeEarlyExercisePremium() {

        double premium    = 0.0;
        double T          = engine.getT();
        double rate       = engine.getRate();
        double strike     = engine.getStrike();
        double spot       = engine.getSpot();

        // Loop through each dividend
        for (var entry : divSchedule.getDividends().entrySet()) {

            double exDate    = entry.getKey();
            double divAmount = entry.getValue();

            // Only check dividends before expiry
            if (exDate >= T) continue;

            // Time remaining after dividend
            double timeRemaining = T - exDate;

            // Interest cost of holding position
            double interestCost = rate * strike * timeRemaining;

            // Check early exercise condition
            if (divAmount > interestCost) {

                // Early exercise IS optimal
                // Premium approximation: fraction of spot price
                // This is a simplified BAW approximation
                double exerciseProbability = divAmount / spot;
                premium += exerciseProbability * spot * 0.01;

                System.out.printf(
                    "  → Early exercise optimal at t=%.2f: " +
                    "Dividend $%.2f > Interest cost $%.2f%n",
                    exDate, divAmount, interestCost
                );
            }
        }

        return premium;
    }

    // ─────────────────────────────────────────────────────────
    // Compare European vs American Price
    // American price is always >= European price
    // ─────────────────────────────────────────────────────────
    public void printComparison() {

        double european = engine.priceDiscreteDividends(divSchedule, curve);
        double american = priceAmericanCall();
        double premium  = american - european;

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     European vs American Prices      ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf ("║  European Call Price  :  %8.4f    ║%n", european);
        System.out.printf ("║  American Call Price  :  %8.4f    ║%n", american);
        System.out.printf ("║  Early Exercise Premium: %8.4f    ║%n", premium);
        System.out.println("╚══════════════════════════════════════╝");
    }
}