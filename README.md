\# 📈 Dividend-Adjusted Black-Scholes Pricing Engine

\## 📌 Overview



Equity derivatives frequently involve discrete dividend payments.

This engine properly adjusts pricing to handle both continuous

dividend yield and discrete cash dividends — essential for

accurate valuations in real trading desks.



---



\## ✨ Features



\- ✅ European Call pricing with \*\*continuous dividend yield\*\* (Merton 1973)

\- ✅ European Call pricing with \*\*discrete cash dividends\*\* (Forward Adjustment)

\- ✅ Full \*\*Greeks\*\*: Delta, Gamma, Vega, Theta, Rho

\- ✅ \*\*American option\*\* early exercise detection

\- ✅ Comparison: continuous yield vs discrete dividend methods



---



\## 📐 Mathematical Model



\*\*Continuous Dividend Yield:\*\*

```

d1 = \[ ln(S/K) + (r - q + σ²/2) \* T ] / (σ \* √T)

d2 = d1 - σ \* √T

Call = S \* e^(-qT) \* N(d1) - K \* e^(-rT) \* N(d2)

```



\*\*Discrete Dividend (Forward Adjustment):\*\*

```

S\_adj = S0 - Σ PV(Dividends)

Then apply standard Black-Scholes on S\_adj

```



---



\## 🗂️ Project Structure

```

equity\_dividend\_bs\_java/

├── pom.xml

├── README.md

├── data/

│   └── dividend\_schedules.csv

└── src/

&nbsp;   ├── main/java/com/quant/equitybs/

&nbsp;   │   ├── Main.java

&nbsp;   │   ├── core/

&nbsp;   │   │   ├── BlackScholesEngine.java

&nbsp;   │   │   ├── DiscountCurve.java

&nbsp;   │   │   └── DividendSchedule.java

&nbsp;   │   ├── model/

&nbsp;   │   │   └── Greeks.java

&nbsp;   │   └── pricer/

&nbsp;   │       └── AmericanPricer.java

&nbsp;   └── test/java/com/quant/equitybs/

&nbsp;       ├── BlackScholesTest.java

&nbsp;       └── GreeksTest.java

```



---



\## 🚀 How to Run



\*\*1. Clone the repository\*\*

```bash

git clone https://github.com/rupeshgoswami/equity\_dividend\_bs\_java.git

cd equity\_dividend\_bs\_java

```



\*\*2. Compile the project\*\*

```bash

mvn compile

```



\*\*3. Run the pricing engine\*\*

```bash

mvn exec:java

```



---



\## 📊 Sample Output

```

===========================================

&nbsp; Dividend-Adjusted Black-Scholes Engine

===========================================



SCENARIO 1: Continuous Dividend Yield (3%)

&nbsp; ✓ Call Price = $7.2345



SCENARIO 2: Discrete Cash Dividend ($2 at 6 months)

&nbsp; PV of Dividend : $1.9506

&nbsp; Adjusted Spot  : $98.0494

&nbsp; ✓ Call Price = $6.8901



SCENARIO 3: Option Greeks

&nbsp; Delta :  0.4823

&nbsp; Gamma :  0.0198

&nbsp; Vega  : 38.4521

&nbsp; Theta : -5.1234

&nbsp; Rho   : 33.2156



SCENARIO 4: American vs European

&nbsp; European Price   : 6.8901

&nbsp; American Price   : 7.0901

&nbsp; Early Exercise Premium: 0.2000

```



---



\## 🛠️ Tech Stack



| Tool | Version | Purpose |

|------|---------|---------|

| Java | 17 | Core language |

| Apache Commons Math | 3.6.1 | Normal distribution |

| Maven | 3.x | Build \& dependencies |

| JUnit 5 | 5.10.0 | Unit testing |



---



\## 📚 Key Concepts



| Concept | Description |

|---------|-------------|

| Forward Adjustment | Subtract PV of dividends from spot price |

| Continuous Yield | Modify BS drift by dividend yield q |

| Early Exercise | Optimal when Dividend > r × K × (T - t) |

| Greeks | Risk sensitivities used for hedging |



---



\## 👤 Author



\*\*Rupesh Goswami\*\*

\- GitHub: \[@rupeshgoswami](https://github.com/rupeshgoswami)



---



\## 📄 License



This project is licensed under the MIT License.

```

