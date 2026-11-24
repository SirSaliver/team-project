package usecase.pricechange;

/**
 * Input data for the Price Change use case. Validation is done by the interactor so that
 * invalid input is reported through the presenter.
 */
public class PriceChangeInputData {
  private final String ticker;
  private final double amount;
  private final double targetGain;
  private final int maxHoldingPeriod;
  private final int window;
  private final double priceChange;

  /**
   * Creates a new PriceChangeInputData object.
   *
   * @param ticker Ticker of the stock to calculate for
   * @param amount Starting amount in dollars
   * @param targetGain Target gain as a fraction (e.g., 20% would be 0.2)
   * @param maxHoldingPeriod Maximum number of trading days the user wants to hold
   * @param window Number of past months to use to calculate future performance
   * @param priceChange Hypothetical price change on this day as a fraction
   */
  public PriceChangeInputData(String ticker, double amount, double targetGain,
                              int maxHoldingPeriod, int window, double priceChange) {
    this.ticker = ticker;
    this.amount = amount;
    this.targetGain = targetGain;
    this.maxHoldingPeriod = maxHoldingPeriod;
    this.window = window;
    this.priceChange = priceChange;
  }

  public String getTicker() {
    return ticker;
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

  public int getWindow() {
    return window;
  }

  public double getPriceChange() {
    return priceChange;
  }
}
