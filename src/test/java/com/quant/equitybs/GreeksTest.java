package com.quant.equitybs;

import com.quant.equitybs.core.BlackScholesEngine;
import com.quant.equitybs.model.Greeks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for Option Greeks
 *
 * These tests verify all 5 Greeks are
 * mathematically correct:
 *
 * Delta  → between 0 and 1 for calls
 * Gamma  → always positive
 * Vega   → always positive
 * Theta  → usually negative (time decay)
 * Rho    → positive for calls
 */
class GreeksTest {

    private static final double TOLERANCE = 0.01;

    // ─────────────────────────────────────────────────────
    // DELTA TESTS
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Delta must be between 0 and 1 for call options")
    void testDeltaBetweenZeroAndOne() {
        BlackScholesEngine engine =
            new BlackScholesEngine(100, 105, 1.0, 0.05, 0.20);

        Greeks greeks = engine.computeGreeks(0.03);

        System.out.printf("Delta: %.4f%n", greeks.getDelta());
        assertTrue(greeks.getDelta() > 0 && greeks.getDelta() < 1,
            "Delta must be between 0 and 1");
    }

    @Test
    @DisplayName("Deep ITM call should have Delta close to 1")
    void testDeepITMDeltaCloseToOne() {
        // Deep In-The-Money: S=200, K=100
        BlackScholesEngine engine =
            new BlackScholesEngine(200, 100, 1.0, 0.05, 0.20);

        Greeks greeks = engine.computeGreeks(0.0);

        System.out.printf("Deep ITM Delta: %.4f%n", greeks.getDelta());
        assertTrue(greeks.getDelta() > 0.9,
            "Deep ITM delta should be close to 1");
    }

    @Test
    @DisplayName("Deep OTM call should have Delta close to 0")
    void testDeepOTMDeltaCloseToZero() {
        // Deep Out-of-The-Money: S=50, K=200
        BlackScholesEngine engine =
            new BlackScholesEngine(50, 200, 1.0, 0.05, 0.20);

        Greeks greeks = engine.computeGreeks(0.0);

        System.out.printf("Deep OTM Delta: %.4f%n", greeks.getDelta());
        assertTrue(greeks.getDelta() < 0.1,
            "Deep OTM delta should be close to 0");
    }

    // ─────────────────────────────────────────────────────
    // GAMMA TESTS
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Gamma must always be positive")
    void testGammaAlwaysPositive() {
        BlackScholesEngine engine =
            new BlackScholesEngine(100, 105, 1.0, 0.05, 0.20);

        Greeks greeks = engine.computeGreeks(0.03);

        System.out.printf("Gamma: %.4f%n", greeks.getGamma());
        assertTrue(greeks.getGamma() > 0,
            "Gamma must always be positive");
    }

    @Test
    @DisplayName("ATM option should have highest Gamma")
    void testATMHasHighestGamma() {
        // ATM: S = K = 100
        BlackScholesEngine atmEngine =
            new BlackScholesEngine(100, 100, 1.0, 0.05, 0.20);
        // OTM: S=100, K=150
        BlackScholesEngine otmEngine =
            new BlackScholesEngine(100, 150, 1.0, 0.05, 0.20);

        Greeks atmGreeks = atmEngine.computeGreeks(0.0);
        Greeks otmGreeks = otmEngine.computeGreeks(0.0);

        System.out.printf("ATM Gamma: %.4f%n", atmGreeks.getGamma());
        System.out.printf("OTM Gamma: %.4f%n", otmGreeks.getGamma());

        assertTrue(atmGreeks.getGamma() > otmGreeks.getGamma(),
            "ATM option should have highest gamma");
    }

    // ─────────────────────────────────────────────────────
    // VEGA TESTS
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Vega must always be positive")
    void testVegaAlwaysPositive() {
        BlackScholesEngine engine =
            new BlackScholesEngine(100, 105, 1.0, 0.05, 0.20);

        Greeks greeks = engine.computeGreeks(0.03);

        System.out.printf("Vega: %.4f%n", greeks.getVega());
        assertTrue(greeks.getVega() > 0,
            "Vega must always be positive");
    }

    // ─────────────────────────────────────────────────────
    // THETA TESTS
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Theta should be negative for OTM calls (time decay)")
    void testThetaNegativeForOTMCall() {
        // OTM call loses value as time passes
        BlackScholesEngine engine =
            new BlackScholesEngine(100, 110, 1.0, 0.05, 0.20);

        Greeks greeks = engine.computeGreeks(0.0);

        System.out.printf("Theta: %.4f%n", greeks.getTheta());
        assertTrue(greeks.getTheta() < 0,
            "Theta should be negative for OTM calls");
    }

    // ─────────────────────────────────────────────────────
    // RHO TESTS
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Rho must be positive for call options")
    void testRhoPositiveForCalls() {
        BlackScholesEngine engine =
            new BlackScholesEngine(100, 105, 1.0, 0.05, 0.20);

        Greeks greeks = engine.computeGreeks(0.03);

        System.out.printf("Rho: %.4f%n", greeks.getRho());
        assertTrue(greeks.getRho() > 0,
            "Rho must be positive for call options");
    }

    // ─────────────────────────────────────────────────────
    // COMBINED TEST
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("All Greeks should be non-zero for standard option")
    void testAllGreeksNonZero() {
        BlackScholesEngine engine =
            new BlackScholesEngine(100, 105, 1.0, 0.05, 0.20);

        Greeks greeks = engine.computeGreeks(0.03);

        System.out.println("All Greeks:");
        System.out.printf("  Delta : %.4f%n", greeks.getDelta());
        System.out.printf("  Gamma : %.4f%n", greeks.getGamma());
        System.out.printf("  Vega  : %.4f%n", greeks.getVega());
        System.out.printf("  Theta : %.4f%n", greeks.getTheta());
        System.out.printf("  Rho   : %.4f%n", greeks.getRho());

        assertNotEquals(0.0, greeks.getDelta(), "Delta should not be zero");
        assertNotEquals(0.0, greeks.getGamma(), "Gamma should not be zero");
        assertNotEquals(0.0, greeks.getVega(),  "Vega should not be zero");
        assertNotEquals(0.0, greeks.getTheta(), "Theta should not be zero");
        assertNotEquals(0.0, greeks.getRho(),   "Rho should not be zero");
    }
}