package com.quant.equitybs;

import com.quant.equitybs.core.BlackScholesEngine;
import com.quant.equitybs.core.DiscountCurve;
import com.quant.equitybs.core.DividendSchedule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for BlackScholesEngine
 *
 * These tests verify our pricing engine is
 * mathematically correct by checking against
 * known Black-Scholes values.
 *
 * Rule: All prices must be within 0.01 tolerance
 */
class BlackScholesTest {

    // Tolerance for price comparison
    // Document requires price difference < 0.1%
    private static final double TOLERANCE = 0.01;

    // ─────────────────────────────────────────────────────
    // TEST GROUP 1: Continuous Dividend Yield Tests
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("ATM Call with no dividend should match known BS value")
    void testATMCallNoDividend() {
        // At-The-Money: S = K = 100
        // Known BS value for these params = 10.4506
        BlackScholesEngine engine =
            new BlackScholesEngine(100, 100, 1.0, 0.05, 0.20);

        double price = engine.priceContinuousYield(0.0);

        System.out.printf("ATM Call (no div): $%.4f%n", price);
        assertEquals(10.4506, price, TOLERANCE);
    }

    @Test
    @DisplayName("OTM Call with no dividend should be cheaper than ATM")
    void testOTMCallCheaperThanATM() {
        // Out-of-The-Money: S=100, K=110
        BlackScholesEngine atmEngine =
            new BlackScholesEngine(100, 100, 1.0, 0.05, 0.20);
        BlackScholesEngine otmEngine =
            new BlackScholesEngine(100, 110, 1.0, 0.05, 0.20);

        double atmPrice = atmEngine.priceContinuousYield(0.0);
        double otmPrice = otmEngine.priceContinuousYield(0.0);

        System.out.printf("ATM Call: $%.4f%n", atmPrice);
        System.out.printf("OTM Call: $%.4f%n", otmPrice);

        // OTM must always be cheaper than ATM
        assertTrue(otmPrice < atmPrice,
            "OTM call should be cheaper than ATM call");
    }

    @Test
    @DisplayName("Higher dividend yield should reduce call price")
    void testHigherDividendReducesCallPrice() {
        BlackScholesEngine engine =
            new BlackScholesEngine(100, 105, 1.0, 0.05, 0.20);

        double priceNoDiv  = engine.priceContinuousYield(0.0);
        double priceLowDiv = engine.priceContinuousYield(0.03);
        double priceHighDiv= engine.priceContinuousYield(0.06);

        System.out.printf("No dividend  : $%.4f%n", priceNoDiv);
        System.out.printf("3%% dividend  : $%.4f%n", priceLowDiv);
        System.out.printf("6%% dividend  : $%.4f%n", priceHighDiv);

        // Higher dividend = lower call price
        assertTrue(priceNoDiv > priceLowDiv,
            "No dividend should be more expensive than 3% yield");
        assertTrue(priceLowDiv > priceHighDiv,
            "3% yield should be more expensive than 6% yield");
    }

    @Test
    @DisplayName("Call price must always be positive")
    void testCallPriceAlwaysPositive() {
        BlackScholesEngine engine =
            new BlackScholesEngine(100, 105, 1.0, 0.05, 0.20);

        double price = engine.priceContinuousYield(0.03);

        System.out.printf("Call price: $%.4f%n", price);
        assertTrue(price > 0, "Call price must always be positive");
    }

    @Test
    @DisplayName("Higher volatility should increase call price")
    void testHigherVolatilityIncreasesPrice() {
        BlackScholesEngine lowVolEngine =
            new BlackScholesEngine(100, 105, 1.0, 0.05, 0.10);
        BlackScholesEngine highVolEngine =
            new BlackScholesEngine(100, 105, 1.0, 0.05, 0.40);

        double lowVolPrice  = lowVolEngine.priceContinuousYield(0.0);
        double highVolPrice = highVolEngine.priceContinuousYield(0.0);

        System.out.printf("Low vol  (10%%): $%.4f%n", lowVolPrice);
        System.out.printf("High vol (40%%): $%.4f%n", highVolPrice);

        // Higher vol = higher option price
        assertTrue(highVolPrice > lowVolPrice,
            "Higher volatility should increase call price");
    }

    // ─────────────────────────────────────────────────────
    // TEST GROUP 2: Discrete Dividend Tests
    // ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Discrete dividend should reduce call price vs no dividend")
    void testDiscreteDividendReducesPrice() {
        BlackScholesEngine engine =
            new BlackScholesEngine(100, 105, 1.0, 0.05, 0.20);

        DiscountCurve curve = new DiscountCurve(0.05);
        DividendSchedule divs = new DividendSchedule();
        divs.addDividend(0.5, 2.0); // $2 at 6 months

        double noDiv   = engine.priceContinuousYield(0.0);
        double withDiv = engine.priceDiscreteDividends(divs, curve);

        System.out.printf("No dividend    : $%.4f%n", noDiv);
        System.out.printf("$2 discrete div: $%.4f%n", withDiv);

        // Dividend reduces call price
        assertTrue(withDiv < noDiv,
            "Discrete dividend should reduce call price");
    }

    @Test
    @DisplayName("Larger dividend should reduce price more")
    void testLargerDividendReducesPriceMore() {
        BlackScholesEngine engine =
            new BlackScholesEngine(100, 105, 1.0, 0.05, 0.20);

        DiscountCurve curve = new DiscountCurve(0.05);

        DividendSchedule smallDiv = new DividendSchedule();
        smallDiv.addDividend(0.5, 1.0); // $1 dividend

        DividendSchedule largeDiv = new DividendSchedule();
        largeDiv.addDividend(0.5, 3.0); // $3 dividend

        double smallDivPrice = engine.priceDiscreteDividends(smallDiv, curve);
        double largeDivPrice = engine.priceDiscreteDividends(largeDiv, curve);

        System.out.printf("$1 dividend: $%.4f%n", smallDivPrice);
        System.out.printf("$3 dividend: $%.4f%n", largeDivPrice);

        // Larger dividend = lower call price
        assertTrue(largeDivPrice < smallDivPrice,
            "Larger dividend should reduce price more");
    }

    @Test
    @DisplayName("Dividend PV exceeding spot should throw exception")
    void testExcessiveDividendThrowsException() {
        // Spot = 100, dividend = $200 (impossible!)
        BlackScholesEngine engine =
            new BlackScholesEngine(100, 105, 1.0, 0.05, 0.20);

        DiscountCurve curve = new DiscountCurve(0.05);
        DividendSchedule divs = new DividendSchedule();
        divs.addDividend(0.5, 200.0); // $200 dividend on $100 stock!

        // Should throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> {
            engine.priceDiscreteDividends(divs, curve);
        });

        System.out.println("✓ Exception correctly thrown for excessive dividend");
    }
}