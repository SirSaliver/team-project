package interfaceadapter.pricechange;

import usecase.pricechange.PriceChangeInputBoundary;
import usecase.pricechange.PriceChangeInputData;

/**
 * The controller for the Price Change use case.
 */
public class PriceChangeController {
  private final PriceChangeInputBoundary interactor;

  /**
   * Creates a new Price Change controller.
   *
   * @param interactor Interactor for the use case
   */
  public PriceChangeController(PriceChangeInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Executes the Price Change use case.
   *
   * @param ticker Ticker of the stock to calculate for
   * @param amount Starting amount in dollars
   * @param targetGainPercent Target gain in percent (e.g., 20% would be 20.0)
   * @param maxHoldingPeriod Maximum number of trading days the user wants to hold
   * @param windowMonths Number of past months to use to calculate future performance
   * @param priceChangePercent Hypothetical price change in percent (e.g., -10% would be -10.0)
   */
  public void execute(String ticker, double amount, double targetGainPercent,
                      int maxHoldingPeriod, int windowMonths, double priceChangePercent) {
    interactor.execute(new PriceChangeInputData(ticker, amount, targetGainPercent / 100.0,
            maxHoldingPeriod, windowMonths, priceChangePercent / 100.0));
  }
}
