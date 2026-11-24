package usecase.addtowatchlist;

import java.util.List;

/**
 * The output data for a successful search in the Add To Watchlist Use Case.
 */
public class AddToWatchlistSearchOutputData {
  private final String query;
  private final List<StockSearchResult> results;

  /**
   * Creates the output data.
   *
   * @param query The trimmed text that was searched for
   * @param results The matching stocks, best match first; never empty
   */
  public AddToWatchlistSearchOutputData(String query, List<StockSearchResult> results) {
    this.query = query;
    this.results = List.copyOf(results);
  }

  public String getQuery() {
    return query;
  }

  public List<StockSearchResult> getResults() {
    return results;
  }
}
