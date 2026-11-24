package usecase.addtowatchlist;

import java.util.List;

/**
 * The output data for a successful addition in the Add To Watchlist Use Case.
 */
public class AddToWatchlistOutputData {
  private final String addedTicker;
  private final List<String> watchlistTickers;

  /**
   * Creates the output data.
   *
   * @param addedTicker The normalized ticker that was added
   * @param watchlistTickers Every ticker in the watchlist after the addition
   */
  public AddToWatchlistOutputData(String addedTicker, List<String> watchlistTickers) {
    this.addedTicker = addedTicker;
    this.watchlistTickers = List.copyOf(watchlistTickers);
  }

  public String getAddedTicker() {
    return addedTicker;
  }

  public List<String> getWatchlistTickers() {
    return watchlistTickers;
  }
}
