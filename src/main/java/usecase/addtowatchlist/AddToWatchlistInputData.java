package usecase.addtowatchlist;

/**
 * The input data for adding a stock in the Add To Watchlist Use Case.
 */
public class AddToWatchlistInputData {
  private final String ticker;

  /**
   * Creates the input data.
   *
   * @param ticker The ticker symbol of the search result the user chose to add
   */
  public AddToWatchlistInputData(String ticker) {
    this.ticker = ticker;
  }

  String getTicker() {
    return ticker;
  }
}
