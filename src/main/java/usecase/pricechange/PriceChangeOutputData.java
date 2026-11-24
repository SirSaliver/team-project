package usecase.pricechange;

import usecase.shared.projection.SellProjection;

/**
 * Output data for the Price Change use case.
 */
public class PriceChangeOutputData {
  private final String ticker;
  private final SellProjection projection;
  private final double sellNowResult;
  private final boolean holdingBetter;
  private final boolean holdingBetterAtLow;

  /**
   * Constructs a new PriceChangeOutputData.
   *
   * @param ticker Ticker of the stock
   * @param projection Calculated selling projection, starting from the changed price
   * @param sellNowResult Profit or loss, relative to the purchase price, if the user sold at the
   *                      changed price right away
   * @param holdingBetter Whether holding is expected to return more than selling now
   * @param holdingBetterAtLow Whether holding still returns more than selling now at the low end
   *                           of the projected sell price range
   */
  public PriceChangeOutputData(String ticker, SellProjection projection, double sellNowResult,
                               boolean holdingBetter, boolean holdingBetterAtLow) {
    this.ticker = ticker;
    this.projection = projection;
    this.sellNowResult = sellNowResult;
    this.holdingBetter = holdingBetter;
    this.holdingBetterAtLow = holdingBetterAtLow;
  }

  public String getTicker() {
    return ticker;
  }

  public SellProjection getProjection() {
    return this.projection;
  }

  public double getSellNowResult() {
    return sellNowResult;
  }

  public boolean isHoldingBetter() {
    return holdingBetter;
  }

  public boolean isHoldingBetterAtLow() {
    return holdingBetterAtLow;
  }
}
