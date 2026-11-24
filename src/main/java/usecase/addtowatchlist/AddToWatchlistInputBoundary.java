package usecase.addtowatchlist;

/**
 * The Add To Watchlist Use Case.
 */
public interface AddToWatchlistInputBoundary {
  /**
   * Searches for stocks matching a ticker symbol or company name.
   *
   * @param inputData The text to search for
   */
  void search(AddToWatchlistSearchInputData inputData);

  /**
   * Adds a stock from the search results to the watchlist.
   *
   * @param inputData The stock to add
   */
  void addToWatchlist(AddToWatchlistInputData inputData);
}
