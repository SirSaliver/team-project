package usecase.setriskprofile;

import usecase.shared.projection.SellProjection;

/**
 * Output data for the Set Risk Profile use case.
 */
public class SetRiskProfileOutputData {
  private final String ticker;
  private final SellProjection projection;
  private final double amount;
  private final double targetGain;
  private final int maxHoldingPeriod;
  private final int windowMonths;

  /**
   * Constructs a new SetRiskProfileOutputData.
   *
   * @param ticker Ticker of the stock
   * @param projection Calculated selling projection
   * @param amount Dollar amount invested
   * @param targetGain Target gain as a fraction (e.g., 20% would be 0.2)
   * @param maxHoldingPeriod Maximum holding period in trading days
   * @param windowMonths Number of past months of prices the estimate was made from
   */
  public SetRiskProfileOutputData(String ticker, SellProjection projection, double amount,
                                  double targetGain, int maxHoldingPeriod, int windowMonths) {
    this.ticker = ticker;
    this.projection = projection;
    this.amount = amount;
    this.targetGain = targetGain;
    this.maxHoldingPeriod = maxHoldingPeriod;
    this.windowMonths = windowMonths;
  }

  public String getTicker() {
    return ticker;
  }

  public SellProjection getProjection() {
    return this.projection;
  }

  public double getAmount() {
    return amount;
  }

  public double getTargetGain() {
    return targetGain;
  }

  public int getMaxHoldingPeriod() {
    return maxHoldingPeriod;
  }

  public int getWindowMonths() {
    return windowMonths;
  }
}
