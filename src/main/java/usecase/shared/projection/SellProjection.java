package usecase.shared.projection;

import java.time.LocalDate;

/**
 * Shared fields for the Set Risk Profile and Price Change output data. Since both use cases
 * return conceptually the same data, they are grouped in this class to deduplicate. A
 * SellProjection contains a {@link SellEstimate} and groups it with time-dependent context,
 * projecting it into the future.
 */
public class SellProjection {
  private final double startPrice;
  private final LocalDate sellDate;
  private final double sellPriceLow;
  private final double sellPriceHigh;
  private final SellEstimate estimate;

  /**
   * Creates a new SellProjection object.
   *
   * @param startPrice Price of a single share of the stock when bought (latest close)
   * @param sellDate Calendar day to sell the stock
   * @param estimate Estimated data from a sell strategy
   * @param sellPriceLow Lower bound of the projected sell price interval
   * @param sellPriceHigh Upper bound of the projected sell price interval
   */
  public SellProjection(double startPrice, LocalDate sellDate, double sellPriceLow,
                        double sellPriceHigh, SellEstimate estimate) {
    this.startPrice = startPrice;
    this.sellDate = sellDate;
    this.sellPriceLow = sellPriceLow;
    this.sellPriceHigh = sellPriceHigh;
    this.estimate = estimate;
  }

  public double getStartPrice() {
    return startPrice;
  }

  public LocalDate getSellDate() {
    return sellDate;
  }

  public double getSellPriceLow() {
    return sellPriceLow;
  }

  public double getSellPriceHigh() {
    return sellPriceHigh;
  }

  public SellEstimate getEstimate() {
    return estimate;
  }
}
