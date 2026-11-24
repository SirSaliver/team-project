package usecase.addtowatchlist;

/**
 * The input data for searching in the Add To Watchlist Use Case.
 */
public class AddToWatchlistSearchInputData {
  private final String query;

  /**
   * Creates the input data.
   *
   * @param query The ticker symbol or company name the user entered, exactly as typed
   */
  public AddToWatchlistSearchInputData(String query) {
    this.query = query;
  }

  String getQuery() {
    return query;
  }
}
