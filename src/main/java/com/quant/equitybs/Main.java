package com.quant.equitybs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.quant.equitybs.core.BlackScholesEngine;
import com.quant.equitybs.core.DiscountCurve;
import com.quant.equitybs.model.Greeks;
import com.quant.equitybs.core.DividendSchedule;

@SpringBootApplication
@RestController
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "Dividend-Adjusted Black-Scholes Engine is running!";
    }

    @GetMapping("/price")
    public String price() {
        double spot   = 100.0;
        double strike = 105.0;
        double T      = 1.0;
        double rate   = 0.05;
        double vol    = 0.20;
        double q      = 0.03;

        BlackScholesEngine engine = new BlackScholesEngine(spot, strike, T, rate, vol);
        DiscountCurve curve = new DiscountCurve(rate);

        double contPrice = engine.priceContinuousYield(q);

        DividendSchedule divSchedule = new DividendSchedule();
        divSchedule.addDividend(0.5, 2.0);
        double divPV = divSchedule.presentValue(T, curve);
        double discPrice = engine.priceDiscreteDividends(divSchedule, curve);

        Greeks greeks = engine.computeGreeks(q);

        StringBuilder sb = new StringBuilder();
        sb.append("=== Dividend-Adjusted Black-Scholes Engine ===\n\n");
        sb.append("Stock Price  : $").append(spot).append("\n");
        sb.append("Strike Price : $").append(strike).append("\n");
        sb.append("Expiry       : ").append(T).append(" year\n");
        sb.append("Rate         : ").append(rate * 100).append("%\n");
        sb.append("Volatility   : ").append(vol * 100).append("%\n\n");
        sb.append("SCENARIO 1 - Continuous Dividend Yield (3%):\n");
        sb.append("Call Price   : $").append(String.format("%.4f", contPrice)).append("\n\n");
        sb.append("SCENARIO 2 - Discrete Cash Dividend ($2 at 6 months):\n");
        sb.append("PV of Dividend: $").append(String.format("%.4f", divPV)).append("\n");
        sb.append("Call Price    : $").append(String.format("%.4f", discPrice)).append("\n\n");
        sb.append("SCENARIO 3 - Greeks:\n");
        sb.append("Delta: ").append(String.format("%.4f", greeks.delta)).append("\n");
        sb.append("Gamma: ").append(String.format("%.4f", greeks.gamma)).append("\n");
        sb.append("Vega : ").append(String.format("%.4f", greeks.vega)).append("\n");
        sb.append("Theta: ").append(String.format("%.4f", greeks.theta)).append("\n");
        sb.append("Rho  : ").append(String.format("%.4f", greeks.rho)).append("\n");

        return sb.toString().replace("\n", "<br>");
    }
}