package usecase.shared.projection;

import entities.GrowthStats;
import entities.RiskProfile;

/**
 * Estimator that calculates the holding period and sell date of a stock from the user's target
 * gain of the stock.
 */
public class TargetGainSellStrategy implements SellStrategy {
  /**
   * Calculates the projected price of a stock t days from now.
   *
   * @param startPrice Starting price of one share
   * @param mu Average daily log return
   * @param t Number of days in holding period
   * @return Projected price of one share after the specified holding period
   */
  private double getProjectedPrice(double startPrice, double mu, int t) {
    return startPrice * Math.exp(mu * t);
  }

  @Override
  public SellEstimate estimate(double amount, double purchasePrice, double currentPrice,
                               GrowthStats stats, RiskProfile profile) {
    // Only whole shares can be bought, and they are bought at the purchase price
    int shares = (int) (amount / purchasePrice);
    double targetPrice = purchasePrice * (1 + profile.getTargetGain());
    double mu = stats.getAverageDailyReturn();
    int maxHoldPeriod = profile.getMaxHoldingPeriod();

    int holdPeriod = maxHoldPeriod;
    boolean targetHit = false;
    if (currentPrice >= targetPrice) {
      // The target has already been reached, so there is no reason to keep holding
      holdPeriod = 0;
      targetHit = true;
    } else if (mu > 0) {
      // Result of solving P_current * e^(mu*t) = P_purchase(1 + g) for t
      double daysToTarget = Math.ceil(Math.log(targetPrice / currentPrice) / mu);
      if (daysToTarget <= maxHoldPeriod) {
        holdPeriod = (int) daysToTarget;
        targetHit = true;
      }
    }

    double projectedPrice = getProjectedPrice(currentPrice, mu, holdPeriod);
    double error = stats.getVolatility() * Math.sqrt(holdPeriod);
    double returns = shares * (projectedPrice - purchasePrice);
    return new SellEstimate(holdPeriod, error, projectedPrice, shares, returns, targetHit);
  }
}
