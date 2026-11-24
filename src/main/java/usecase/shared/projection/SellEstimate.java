package usecase.shared.projection;

/**
 * The output of an algorithm that predicts when the user should sell a stock.
 */
public class SellEstimate {
  private final int holdPeriod;
  private final double sellPrice;
  private final double error;
  private final int shares;
  private final double returns;
  private final boolean targetReached;

  /**
   * Creates a new SellEstimate object.
   *
   * @param holdPeriod Holding period of the specified stock in trading days
   * @param error Variability in projected sell price: volatility * sqrt(holding period)
   * @param sellPrice Price of a single share of the stock at the end of the holding period
   * @param shares Number of whole shares held
   * @param returns Dollar returns, relative to the purchase price, after selling at the end of
   *                the holding period
   * @param targetReached Whether the user's specified target gain was reached
   */
  public SellEstimate(int holdPeriod, double error, double sellPrice, int shares,
                      double returns, boolean targetReached) {
    this.holdPeriod = holdPeriod;
    this.error = error;
    this.sellPrice = sellPrice;
    this.shares = shares;
    this.returns = returns;
    this.targetReached = targetReached;
  }

  public int getHoldPeriod() {
    return holdPeriod;
  }

  public double getSellPrice() {
    return sellPrice;
  }

  public double getError() {
    return error;
  }

  public int getShares() {
    return shares;
  }

  public double getReturns() {
    return returns;
  }

  public boolean isTargetReached() {
    return targetReached;
  }
}
