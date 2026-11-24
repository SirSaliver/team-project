package usecase.shared.projection;

import entities.GrowthStats;
import entities.RiskProfile;

/**
 * Interface describing an algorithm to estimate the sell date of a stock given its stats and
 * the user's risk profile.
 */
public interface SellStrategy {
  /**
   * Estimates the holding period and expected return of a stock. The position is bought at
   * {@code purchasePrice}, and the projection into the future starts from {@code currentPrice}.
   * Shares, target gain and returns are all measured relative to the purchase price.
   *
   * @param amount Dollar amount invested at the purchase price
   * @param purchasePrice Price of one share when it was bought
   * @param currentPrice Price of one share now, from which the projection starts
   * @param stats Growth statistics of the stock in the past
   * @param profile The user's risk profile
   * @return An estimate of the holding period and return
   */
  SellEstimate estimate(double amount, double purchasePrice, double currentPrice,
                        GrowthStats stats, RiskProfile profile);
}
