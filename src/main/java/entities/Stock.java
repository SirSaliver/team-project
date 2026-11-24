package entities;

/**
 * An entity representing a stock. This entity is immutable once created, so it has no mutator
 * methods.
 */
public class Stock {
  private final String ticker;

  /**
   * Constructs a new Stock object.
   *
   * @param ticker The ticker of the stock
   * @throws IllegalArgumentException If the ticker is null, empty, or only whitespace
   */
  public Stock(String ticker) {
    if (ticker == null || ticker.isBlank()) {
      throw new IllegalArgumentException("Ticker cannot be empty");
    }

    this.ticker = ticker;
  }

  public String getTicker() {
    return ticker;
  }

  /**
   * Two stocks are equal when they have the same ticker.
   *
   * @param other The object to compare with
   * @return Whether other is a Stock with the same ticker
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Stock)) {
      return false;
    }
    return ticker.equals(((Stock) other).ticker);
  }

  @Override
  public int hashCode() {
    return ticker.hashCode();
  }
}
