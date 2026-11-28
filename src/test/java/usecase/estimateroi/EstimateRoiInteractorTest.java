package usecase.estimateroi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link EstimateRoiInteractor}.
 */
class EstimateRoiInteractorTest {
  private static final double DELTA = 1e-6;

  /** A fake data access object that returns fixed prices, or throws if prices is null. */
  private static class FakeDataAccess implements EstimateRoiDataAccessInterface {
    private final List<Double> prices;
    private String requestedTicker;
    private int requestedHistoryLength;

    FakeDataAccess(List<Double> prices) {
      this.prices = prices;
    }

    @Override
    public List<Double> getHistoricalPrices(String ticker, int historyLength) {
      requestedTicker = ticker;
      requestedHistoryLength = historyLength;
      if (prices == null) {
        throw new IllegalStateException("API unavailable");
      }
      return prices;
    }
  }

  /** A fake presenter that records what the interactor sent it. */
  private static class FakePresenter implements EstimateRoiOutputBoundary {
    private EstimateRoiOutputData success;
    private String error;

    @Override
    public void prepareSuccessView(EstimateRoiOutputData outputData) {
      success = outputData;
    }

    @Override
    public void prepareFailView(String errorMessage) {
      error = errorMessage;
    }
  }

  private FakePresenter run(List<Double> prices, double amount, int historyLength) {
    final FakePresenter presenter = new FakePresenter();
    new EstimateRoiInteractor(new FakeDataAccess(prices), presenter)
            .execute(new EstimateRoiInputData("AAPL", amount, historyLength));
    return presenter;
  }

  @Test
  void oneYearGainIsProjectedForward() {
    // $1100 buys exactly 10 shares at $110, and the price grew 10% over the last year.
    final FakePresenter presenter = run(List.of(100.0, 105.0, 110.0), 1100, 1);

    assertNull(presenter.error);
    assertEquals(10, presenter.success.getShares());
    assertEquals(110.0, presenter.success.getSharePrice(), DELTA);
    assertEquals(1210.0, presenter.success.getEstimatedValue(), DELTA);
    assertEquals(110.0, presenter.success.getProfit(), DELTA);
    assertEquals(10.0, presenter.success.getPercentageReturn(), DELTA);
    assertEquals(1, presenter.success.getHistoryLength());
  }

  @Test
  void onlyWholeSharesGrowAndLeftoverCashStaysTheSame() {
    // $1000 buys 9 shares at $110 ($990); the leftover $10 does not grow.
    final FakePresenter presenter = run(List.of(100.0, 110.0), 1000, 1);

    assertEquals(9, presenter.success.getShares());
    assertEquals(99.0, presenter.success.getProfit(), DELTA);
    assertEquals(1099.0, presenter.success.getEstimatedValue(), DELTA);
    assertEquals(9.9, presenter.success.getPercentageReturn(), DELTA);
  }

  @Test
  void multiYearHistoryUsesAverageYearlyGrowth() {
    // The price doubles over 2 years, so it grows by sqrt(2) - 1 (about 41.4%) per year.
    // $1000 buys exactly 10 shares at $100.
    final FakePresenter presenter = run(List.of(50.0, 100.0), 1000, 2);

    final double yearlyGrowth = Math.sqrt(2) - 1;
    assertEquals(10, presenter.success.getShares());
    assertEquals(1000 * (1 + yearlyGrowth), presenter.success.getEstimatedValue(), DELTA);
    assertEquals(yearlyGrowth * 100, presenter.success.getPercentageReturn(), DELTA);
    assertEquals(2, presenter.success.getHistoryLength());
  }

  @Test
  void priceDropGivesLoss() {
    // $600 buys 4 shares at $150, and the price fell 25% over the last year.
    final FakePresenter presenter = run(List.of(200.0, 150.0), 600, 1);

    assertEquals(4, presenter.success.getShares());
    assertEquals(450.0, presenter.success.getEstimatedValue(), DELTA);
    assertEquals(-150.0, presenter.success.getProfit(), DELTA);
    assertEquals(-25.0, presenter.success.getPercentageReturn(), DELTA);
  }

  @Test
  void amountBelowOneShareIsRejected() {
    // $2 cannot buy a single $110 share (the case from the PR review).
    final FakePresenter presenter = run(List.of(100.0, 110.0), 2, 1);

    assertNull(presenter.success);
    assertEquals("Amount must be at least the price of one share ($110.00).", presenter.error);
  }

  @Test
  void amountOfExactlyOneShareIsAccepted() {
    final FakePresenter presenter = run(List.of(100.0, 110.0), 110, 1);

    assertNull(presenter.error);
    assertEquals(1, presenter.success.getShares());
  }

  @Test
  void passesTickerAndHistoryLengthToDataAccess() {
    final FakeDataAccess dataAccess = new FakeDataAccess(List.of(1.0, 2.0));
    new EstimateRoiInteractor(dataAccess, new FakePresenter())
            .execute(new EstimateRoiInputData("MSFT", 100, 3));

    assertEquals("MSFT", dataAccess.requestedTicker);
    assertEquals(3, dataAccess.requestedHistoryLength);
  }

  @Test
  void negativeAmountIsRejected() {
    final FakePresenter presenter = run(List.of(100.0, 110.0), -5, 1);

    assertNull(presenter.success);
    assertEquals("Please enter a positive investment amount.", presenter.error);
  }

  @Test
  void zeroAmountIsRejected() {
    final FakePresenter presenter = run(List.of(100.0, 110.0), 0, 1);

    assertEquals("Please enter a positive investment amount.", presenter.error);
  }

  @Test
  void nanAmountIsRejected() {
    final FakePresenter presenter = run(List.of(100.0, 110.0), Double.NaN, 1);

    assertEquals("Please enter a positive investment amount.", presenter.error);
  }

  @Test
  void invalidHistoryLengthIsRejected() {
    final FakePresenter presenter = run(List.of(100.0, 110.0), 1000, 0);

    assertEquals("Please select a valid history length.", presenter.error);
  }

  @Test
  void emptyHistoryMeansNotEnoughData() {
    final FakePresenter presenter = run(List.of(), 1000, 5);

    assertNull(presenter.success);
    assertEquals("Not enough historical data available.", presenter.error);
  }

  @Test
  void singlePriceMeansNotEnoughData() {
    final FakePresenter presenter = run(List.of(100.0), 1000, 1);

    assertEquals("Not enough historical data available.", presenter.error);
  }

  @Test
  void dataAccessFailureAsksUserToTryAgain() {
    final FakePresenter presenter = run(null, 1000, 1);

    assertNull(presenter.success);
    assertTrue(presenter.error.contains("try again"));
  }

  @Test
  void zeroPriceIsReportedAsInvalid() {
    final FakePresenter presenter = run(List.of(0.0, 110.0), 1000, 1);

    assertEquals("Invalid stock prices.", presenter.error);
  }
}
