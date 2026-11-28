package usecase.estimateroi;

/**
 * The output data for the Estimate ROI use case.
 */
public class EstimateRoiOutputData {
  private final int shares;
  private final double sharePrice;
  private final double estimatedValue;
  private final double profit;
  private final double percentageReturn;
  private final int historyLength;

  /**
   * Creates the output data for a successful estimate.
   *
   * @param shares The number of whole shares the amount buys
   * @param sharePrice The current price of one share
   * @param estimatedValue The estimated value of the investment (shares plus leftover cash) after
   *     one year
   * @param profit The estimated profit (negative for a loss) in dollars
   * @param percentageReturn The estimated profit as a percentage of the amount entered
   * @param historyLength How many years of past prices the estimate is based on
   */
  public EstimateRoiOutputData(int shares, double sharePrice, double estimatedValue,
                               double profit, double percentageReturn, int historyLength) {
    this.shares = shares;
    this.sharePrice = sharePrice;
    this.estimatedValue = estimatedValue;
    this.profit = profit;
    this.percentageReturn = percentageReturn;
    this.historyLength = historyLength;
  }

  public int getShares() {
    return shares;
  }

  public double getSharePrice() {
    return sharePrice;
  }

  public double getEstimatedValue() {
    return estimatedValue;
  }

  public double getProfit() {
    return profit;
  }

  public double getPercentageReturn() {
    return percentageReturn;
  }

  public int getHistoryLength() {
    return historyLength;
  }
}
