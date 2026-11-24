package interfaceadapter.addtowatchlist;

import java.util.List;
import java.util.Set;

/** The search results and status message shown by the Add To Watchlist view. */
public class AddToWatchlistState {
  private final List<Row> rows;
  private final Set<String> watchlistTickers;
  private final String message;
  private final boolean error;

  AddToWatchlistState(List<Row> rows, Set<String> watchlistTickers, String message,
                      boolean error) {
    this.rows = List.copyOf(rows);
    this.watchlistTickers = Set.copyOf(watchlistTickers);
    this.message = message;
    this.error = error;
  }

  /**
   * Returns the search results to display.
   *
   * @return The rows, best match first; empty when there are no results to show
   */
  public List<Row> getRows() {
    return rows;
  }

  /**
   * Returns whether the given ticker is known to be in the watchlist already.
   *
   * @param ticker The ticker to check
   * @return true if this ticker was added during this session
   */
  public boolean isInWatchlist(String ticker) {
    return watchlistTickers.contains(ticker);
  }

  Set<String> getWatchlistTickers() {
    return watchlistTickers;
  }

  public String getMessage() {
    return message;
  }

  public boolean isError() {
    return error;
  }

  /** One search result, formatted for display. */
  public static final class Row {
    private final String ticker;
    private final String companyName;
    private final String exchange;

    Row(String ticker, String companyName, String exchange) {
      this.ticker = ticker;
      this.companyName = companyName;
      this.exchange = exchange;
    }

    public String getTicker() {
      return ticker;
    }

    public String getCompanyName() {
      return companyName;
    }

    public String getExchange() {
      return exchange;
    }
  }
}
