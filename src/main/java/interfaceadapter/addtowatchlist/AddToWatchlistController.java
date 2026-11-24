package interfaceadapter.addtowatchlist;

import usecase.addtowatchlist.AddToWatchlistInputBoundary;
import usecase.addtowatchlist.AddToWatchlistInputData;
import usecase.addtowatchlist.AddToWatchlistSearchInputData;

/**
 * The controller for the Add To Watchlist Use Case.
 */
public class AddToWatchlistController {
  private final AddToWatchlistInputBoundary interactor;

  /**
   * Creates a controller for the Add To Watchlist Use Case.
   *
   * @param interactor The input boundary used to search for and add stocks
   */
  public AddToWatchlistController(AddToWatchlistInputBoundary interactor) {
    this.interactor = interactor;
  }

  /**
   * Searches for stocks by ticker symbol or company name.
   *
   * @param query The text the user entered
   */
  public void search(String query) {
    interactor.search(new AddToWatchlistSearchInputData(query));
  }

  /**
   * Adds the chosen search result to the watchlist.
   *
   * @param ticker The ticker symbol of the chosen stock
   */
  public void addToWatchlist(String ticker) {
    interactor.addToWatchlist(new AddToWatchlistInputData(ticker));
  }
}
