package interfaceadapter.shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Provides common formatters used by the Set Risk Profile and Price Change use cases.
 */
public class ProjectionFormat {
  private static final DateTimeFormatter DATE_FORMAT =
          DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US);

  /**
   * Formats a buying price.
   *
   * @param startPrice Buying price
   * @return Buying price formatted for display
   */
  public static String formatBuyPrice(double startPrice) {
    return formatDollars(startPrice) + " (latest close)";
  }

  /**
   * Formats a sell date.
   *
   * @param sellDate Date of selling a stock
   * @param holdPeriod Number of trading days before the sell date
   * @return The sell date with hold period formatted for display
   */
  public static String formatSellDate(LocalDate sellDate, int holdPeriod) {
    return sellDate.format(DATE_FORMAT) + " (" + holdPeriod + " trading days)";
  }

  /**
   * Formats a dollar amount.
   *
   * @param amount Dollar amount
   * @return Dollar amount formatted for display
   */
  public static String formatDollars(double amount) {
    return String.format(Locale.US, "$%,.2f", amount);
  }

  /**
   * Formats a signed dollar amount (may be negative).
   *
   * @param amount Signed dollar amount
   * @return Signed dollar amount for display
   */
  public static String formatDollarsSigned(double amount) {
    return (amount < 0 ? "-" : "+") + formatDollars(Math.abs(amount));
  }

  /**
   * Formats a price range.
   *
   * @param low Lower bound of the range
   * @param high Upper bound of the range
   * @return The range formatted for display
   */
  public static String formatPriceRange(double low, double high) {
    return formatDollars(low) + " - " + formatDollars(high);
  }

  /**
   * Formats a message of whether the user's target gain will be reached within their
   * holding period.
   *
   * @param reached Whether the target gain will be reached
   * @return The corresponding message
   */
  public static String formatTargetReached(boolean reached) {
    return reached
            ? "Target gain is reached within your maximum holding period."
            : "Target gain is not reached within your maximum holding period; "
            + "this estimate sells at the end of it.";
  }
}
