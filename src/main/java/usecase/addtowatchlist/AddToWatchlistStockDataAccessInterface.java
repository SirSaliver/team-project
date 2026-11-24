package usecase.addtowatchlist;

import java.io.IOException;
import java.util.List;

/**
 * The stock search DAO interface for the Add To Watchlist Use Case.
 */
public interface AddToWatchlistStockDataAccessInterface {
  /**
   * Finds stocks whose ticker symbol or company name matches the query.
   *
   * @param query The text the user searched for
   * @return The matching stocks, or an empty list if none match
   * @throws IOException If the market data provider cannot be reached
   */
  List<StockSearchResult> searchStocks(String query) throws IOException;
}
