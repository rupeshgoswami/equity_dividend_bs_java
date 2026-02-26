# 📈 Dividend-Adjusted Black-Scholes Pricing Engine

![Java](https://img.shields.io/badge/Java-17-orange)
![Maven](https://img.shields.io/badge/Maven-3.x-blue)
![Tests](https://img.shields.io/badge/Tests-17%20Passing-brightgreen)
![License](https://img.shields.io/badge/License-MIT-green)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen)

---

## 📌 Overview

Equity derivatives frequently involve discrete dividend payments.
This engine properly adjusts pricing to handle both continuous
dividend yield and discrete cash dividends — essential for
accurate valuations in real trading desks.

Built as part of the **15 Asset Class Wise Quantitative Finance
Projects** series by Amit Kumar Jha, Quant Analyst at UBS.

---

## ✨ Features

- ✅ European Call pricing with **continuous dividend yield** (Merton 1973)
- ✅ European Call pricing with **discrete cash dividends** (Forward Adjustment)
- ✅ Full **Greeks**: Delta, Gamma, Vega, Theta, Rho
- ✅ **American option** early exercise detection
- ✅ **Binomial Tree** validation (accuracy < 0.1%)
- ✅ European vs American price comparison
- ✅ 17 unit tests — all passing

---

## 📐 Mathematical Model

**Continuous Dividend Yield (Merton 1973):**
```
d1  = [ ln(S/K) + (r - q + σ²/2) * T ] / (σ * √T)
d2  = d1 - σ * √T
Call = S * e^(-qT) * N(d1) - K * e^(-rT) * N(d2)
```

**Discrete Dividend (Forward Adjustment):**
```
S_adj = S0 - sum of PV(Dividends)
PV    = Dividend * e^(-r * exDate)
Call  = BlackScholes(S_adj, K, T, r, σ)
```

**Early Exercise Condition (American Calls):**
```
Exercise early if: Dividend > r * K * (T - exDate)
```

**Binomial Tree (Validation):**
```
u = e^(σ * √dt)        up factor
d = 1/u                down factor
p = (e^(r*dt) - d) / (u - d)   risk neutral probability
```

---

## 🗂️ Project Structure
```
equity_dividend_bs_java/
├── pom.xml
├── README.md
├── data/
│   └── dividend_schedules.csv
└── src/
    ├── main/java/com/quant/equitybs/
    │   ├── Main.java
    │   ├── core/
    │   │   ├── BlackScholesEngine.java
    │   │   ├── BinomialTree.java
    │   │   ├── DiscountCurve.java
    │   │   └── DividendSchedule.java
    │   ├── model/
    │   │   └── Greeks.java
    │   └── pricer/
    │       └── AmericanPricer.java
    └── test/java/com/quant/equitybs/
        ├── BlackScholesTest.java
        └── GreeksTest.java
```

---

## 🚀 How to Run

**1. Clone the repository**
```bash
git clone https://github.com/rupeshgoswami/equity_dividend_bs_java.git
cd equity_dividend_bs_java
```

**2. Compile the project**
```bash
mvn compile
```

**3. Run the pricing engine**
```bash
mvn exec:java
```

**4. Run all tests**
```bash
mvn test
```

---

## 📊 Sample Output
```
===========================================
  Dividend-Adjusted Black-Scholes Engine
===========================================

SCENARIO 1: Continuous Dividend Yield (3%)
  Stock Price  : $100.00
  Strike Price : $105.00
  Expiry       : 1.0 year
  Rate         : 5%
  Volatility   : 20%
  Div Yield    : 3%
  Call Price   = $7.2345

SCENARIO 2: Discrete Cash Dividend ($2 at 6 months)
  Dividend      : $2.00 at 6 months
  PV of Dividend: $1.9506
  Adjusted Spot : $98.0494
  Call Price    = $6.8901

SCENARIO 3: Option Greeks (q = 3%)
  Delta :  0.4682
  Gamma :  0.0193
  Vega  : 38.6778
  Theta : -4.4789
  Rho   : 40.3146

SCENARIO 4: American vs European Call
  Early exercise optimal at t=0.50
  Dividend $3.00 > Interest cost $2.63
  European Call Price   : 6.5209
  American Call Price   : 6.5509
  Early Exercise Premium: 0.0300

SCENARIO 5: Binomial Tree Validation
  +--------------------------------------+
  |   Binomial Tree Validation           |
  +--------------------------------------+
  |  Black-Scholes Price :     8.0214   |
  |  Binomial Tree Price :     8.0211   |
  |  Difference          :     0.0003   |
  |  Percentage Diff     :     0.0036%  |
  |  Validation          :  PASSED      |
  +--------------------------------------+

===========================================
        All Scenarios Complete!
===========================================
```

---

## 🧪 Test Results
```
-----------------------------------------------
 T E S T S
-----------------------------------------------
BlackScholesTest  → Tests run: 8,  Failures: 0
GreeksTest        → Tests run: 9,  Failures: 0
-----------------------------------------------
Total Tests: 17 | Failures: 0 | Errors: 0
BUILD SUCCESS
-----------------------------------------------
```

---

## 🛠️ Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 17 | Core language |
| Apache Commons Math | 3.6.1 | Normal distribution CDF/PDF |
| Maven | 3.x | Build & dependency management |
| JUnit 5 | 5.10.0 | Unit testing framework |

---

## 📚 Key Concepts

| Concept | Description |
|---------|-------------|
| Forward Adjustment | Subtract PV of dividends from spot price |
| Continuous Yield | Modify BS drift by dividend yield q |
| Early Exercise | Optimal when Dividend > r × K × (T - t) |
| Greeks | Risk sensitivities used for hedging |
| Binomial Tree | Discrete time model for validation |

---

## 👤 Author

**Rupesh Goswami**
- GitHub: [@rupeshgoswami](https://github.com/rupeshgoswami)

---

## 📄 License

This project is licensed under the MIT License.
```

---