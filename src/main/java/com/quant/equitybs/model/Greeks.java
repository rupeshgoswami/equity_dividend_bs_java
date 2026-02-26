package com.quant.equitybs.model;

/**
 * Holds all option Greeks
 * Delta  = sensitivity to spot price
 * Gamma  = sensitivity of delta to spot price
 * Vega   = sensitivity to volatility
 * Theta  = sensitivity to time passing
 * Rho    = sensitivity to interest rate
 */
public class Greeks {

    private final double delta;
    private final double gamma;
    private final double vega;
    private final double theta;
    private final double rho;

    public Greeks(double delta, double gamma,
                  double vega, double theta, double rho) {
        this.delta = delta;
        this.gamma = gamma;
        this.vega  = vega;
        this.theta = theta;
        this.rho   = rho;
    }

    public double getDelta() { return delta; }
    public double getGamma() { return gamma; }
    public double getVega()  { return vega;  }
    public double getTheta() { return theta; }
    public double getRho()   { return rho;   }

    @Override
    public String toString() {
        return String.format(
            "Greeks { Delta=%.4f, Gamma=%.4f, Vega=%.4f, Theta=%.4f, Rho=%.4f }",
            delta, gamma, vega, theta, rho
        );
    }
}