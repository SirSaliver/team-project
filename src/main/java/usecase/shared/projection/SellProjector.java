package usecase.shared.projection;

import entities.DailyAggregate;
import entities.GrowthStats;
import entities.RiskProfile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Class that uses a {@link SellProjector} to project the future selling date and price of a stock.
 */
public class SellProjector {
  private final SellStrategy strategy;

  public SellProjector(SellStrategy strategy) {
    this.strategy = strategy;
  }

  /**
   * Adds a number of trading days (weekdays) to a date. Market holidays are not accounted for.
   *
   * @param start The starting date
   * @param tradingDays Number of trading days to add
   * @return The date the given number of trading days after the start date
   */
  private static LocalDate addTradingDays(LocalDate start, int tradingDays) {
    LocalDate date = start;
    int added = 0;
    while (added < tradingDays) {
      date = date.plusDays(1);
      DayOfWeek day = date.getDayOfWeek();
      if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
        added++;
      }
    }
    return date;
  }

  /**
   * Projects a future sell date using price history and a risk profile.
   *
   * @param marketTimeZone Time zone of the market
   * @param history Past price history of the stock
   * @param amount Amount of dollars to invest
   * @param startPrice Starting price of one share
   * @param profile User's risk profile
   * @return A projection containing predicted sell date and projected price (+error) then
   */
  public SellProjection project(ZoneId marketTimeZone, List<DailyAggregate> history, double amount,
                                double startPrice, RiskProfile profile) {
    if (amount < startPrice) {
      throw new IllegalArgumentException(String.format(
              "Amount must be at least the price of one share ($%.2f)", startPrice));
    }

    GrowthStats stats = new GrowthStats(history);
    DailyAggregate latest = history.get(history.size() - 1);

    SellEstimate estimate = strategy.estimate(amount, startPrice, stats, profile);
    LocalDate lastTradingDay = latest.getTime().atZone(marketTimeZone).toLocalDate();
    LocalDate sellDate = addTradingDays(lastTradingDay, estimate.getHoldPeriod());

    // The error is the standard deviation of the log return, so the +/- 1 standard deviation
    // price range is sellPrice * e^(-error) to sellPrice * e^(error).
    double sellPrice = estimate.getSellPrice();
    double error = estimate.getError();
    double sellPriceLow = sellPrice * Math.exp(-error);
    double sellPriceHigh = sellPrice * Math.exp(error);

    return new SellProjection(startPrice, sellDate, sellPriceLow, sellPriceHigh, estimate);
  }
}
