package usecase.addtowatchlist;

import entities.Stock;
import entities.Watchlist;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * The Add To Watchlist Interactor.
 */
public class AddToWatchlistInteractor implements AddToWatchlistInputBoundary {
  private final AddToWatchlistWatchlistDataAccessInterface watchlistDataAccessObject;
  private final AddToWatchlistStockDataAccessInterface stockDataAccessObject;
  private final AddToWatchlistOutputBoundary presenter;

  /**
   * Creates a new AddToWatchlistInteractor.
   *
   * @param watchlistDataAccessInterface Data access object for the saved watchlist
   * @param stockDataAccessInterface Data access object used to search for stocks
   * @param outputBoundary The presenter
   */
  public AddToWatchlistInteractor(
          AddToWatchlistWatchlistDataAccessInterface watchlistDataAccessInterface,
          AddToWatchlistStockDataAccessInterface stockDataAccessInterface,
          AddToWatchlistOutputBoundary outputBoundary) {
    this.watchlistDataAccessObject = watchlistDataAccessInterface;
    this.stockDataAccessObject = stockDataAccessInterface;
    this.presenter = outputBoundary;
  }

  @Override
  public void search(AddToWatchlistSearchInputData inputData) {
    String rawQuery = inputData.getQuery();
    if (rawQuery == null || rawQuery.isBlank()) {
      presenter.prepareSearchFailView("Enter a ticker symbol or company name to search.");
      return;
    }
    String query = rawQuery.strip();

    List<StockSearchResult> results;
    try {
      results = stockDataAccessObject.searchStocks(query);
    } catch (IOException e) {
      presenter.prepareSearchFailView("The search could not be completed. Please try again.");
      return;
    }

    if (results.isEmpty()) {
      presenter.prepareSearchFailView("No stocks were found matching \"" + query + "\".");
      return;
    }
    presenter.prepareSearchSuccessView(
            new AddToWatchlistSearchOutputData(query, exactTickerMatchFirst(results, query)));
  }

  @Override
  public void addToWatchlist(AddToWatchlistInputData inputData) {
    String rawTicker = inputData.getTicker();
    if (rawTicker == null || rawTicker.isBlank()) {
      presenter.prepareAddFailView("Choose a stock from the search results to add.");
      return;
    }
    Stock stock = new Stock(normalizeTicker(rawTicker));
    String notAdded = stock.getTicker() + " was not added to your watchlist. Please try again.";

    Watchlist watchlist;
    try {
      watchlist = watchlistDataAccessObject.readWatchlist();
    } catch (IOException e) {
      presenter.prepareAddFailView(notAdded);
      return;
    }

    if (!watchlist.addStock(stock)) {
      presenter.prepareAddFailView(stock.getTicker() + " is already in your watchlist.");
      return;
    }

    try {
      watchlistDataAccessObject.saveWatchlist(watchlist);
    } catch (IOException e) {
      // The saved watchlist is untouched because the write failed, so nothing needs rolling back.
      presenter.prepareAddFailView(notAdded);
      return;
    }

    List<String> tickers = watchlist.getStocks().stream()
            .map(Stock::getTicker)
            .collect(Collectors.toList());
    presenter.prepareAddSuccessView(new AddToWatchlistOutputData(stock.getTicker(), tickers));
  }

  /**
   * Moves the result whose ticker is exactly the query to the front, so that searching "F"
   * shows Ford before every other company containing the letter F.
   */
  private static List<StockSearchResult> exactTickerMatchFirst(List<StockSearchResult> results,
                                                               String query) {
    String normalizedQuery = normalizeTicker(query);
    List<StockSearchResult> sorted = new ArrayList<>(results);
    sorted.sort(Comparator.comparing(
            result -> !normalizeTicker(result.getTicker()).equals(normalizedQuery)));
    return sorted;
  }

  private static String normalizeTicker(String ticker) {
    return ticker.strip().toUpperCase(Locale.ROOT);
  }
}
