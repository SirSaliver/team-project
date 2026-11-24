package usecase.pricechange;

import entities.DailyAggregate;
import entities.RiskProfile;
import entities.Stock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import usecase.shared.projection.SellEstimate;
import usecase.shared.projection.SellProjection;
import usecase.shared.projection.SellProjector;

/**
 * The Price Change Interactor.
 */
public class PriceChangeInteractor implements PriceChangeInputBoundary {
  private static final ZoneId MARKET_TIME_ZONE = ZoneId.of("America/New_York");

  private final PriceChangeStockDataAccessInterface stockDataAccessObject;
  private final PriceChangeOutputBoundary presenter;
  private final SellProjector projector;

  /**
   * Creates a new PriceChangeInteractor.
   *
   * @param stockDataAccessInterface Data access object for stock price history
   * @param outputBoundary The presenter
   * @param projector Projector algorithm used to estimate when to sell
   */
  public PriceChangeInteractor(
          PriceChangeStockDataAccessInterface stockDataAccessInterface,
          PriceChangeOutputBoundary outputBoundary,
          SellProjector projector) {
    this.stockDataAccessObject = stockDataAccessInterface;
    this.presenter = outputBoundary;
    this.projector = projector;
  }

  @Override
  public void execute(PriceChangeInputData inputData) {
    PriceChangeOutputData outputData;
    try {
      String ticker = inputData.getTicker();
      Stock stock = new Stock(ticker);
      double amount = inputData.getAmount();
      if (amount <= 0) {
        throw new IllegalArgumentException("Amount must be positive");
      }

      double priceChange = inputData.getPriceChange();
      if (priceChange <= -1) {
        throw new IllegalArgumentException("Price change must not cause a negative stock price");
      }

      RiskProfile profile = new RiskProfile(inputData.getTargetGain(),
              inputData.getMaxHoldingPeriod());

      // Fetch stock data according to the price window
      LocalDate to = LocalDate.now(MARKET_TIME_ZONE);
      LocalDate from = to.minusMonths(inputData.getWindow());
      List<DailyAggregate> history;
      try {
        history = stockDataAccessObject.getDailyAggregates(stock, from, to);
      } catch (RuntimeException e) {
        // Error occurred while invoking the data access object
        presenter.prepareFailView("Could not retrieve price data for " + ticker + ": "
                + e.getMessage());
        return;
      }
      if (history == null || history.isEmpty()) {
        throw new IllegalArgumentException("No price data found for " + ticker);
      }

      DailyAggregate latest = history.get(history.size() - 1);

      // The user bought at the latest close, and the price has since moved by priceChange
      double purchasePrice = latest.getClose();
      double changedPrice = purchasePrice * (1 + priceChange);
      SellProjection projection = projector.project(MARKET_TIME_ZONE, history, amount,
              purchasePrice, changedPrice, profile);
      SellEstimate estimate = projection.getEstimate();

      // Both outcomes are measured from the purchase price, so holding is better exactly when
      // the projected sell price beats the changed price
      double sellNowResult = estimate.getShares() * (changedPrice - purchasePrice);
      boolean holdingBetter = estimate.getSellPrice() > changedPrice;
      boolean holdingBetterAtLow = projection.getSellPriceLow() > changedPrice;
      outputData = new PriceChangeOutputData(ticker, projection, sellNowResult, holdingBetter,
              holdingBetterAtLow);
    } catch (IllegalArgumentException e) {
      // Any validation error was encountered
      presenter.prepareFailView(e.getMessage());
      return;
    }

    presenter.prepareSuccessView(outputData);
  }
}
