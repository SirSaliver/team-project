package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An entity representing the user's watchlist. A watchlist never contains the same stock twice.
 */
public class Watchlist {
  private final List<Stock> stocks;

  /**
   * Constructs a new Watchlist object. Duplicate stocks in the given list are dropped.
   *
   * @param stocks The list of stocks in the user's watchlist
   */
  public Watchlist(List<Stock> stocks) {
    this.stocks = new ArrayList<>();
    for (Stock stock : stocks) {
      addStock(stock);
    }
  }

  /**
   * Returns the stocks in this watchlist, in the order they were added.
   *
   * @return An unmodifiable view of the stocks in this watchlist
   */
  public List<Stock> getStocks() {
    return Collections.unmodifiableList(stocks);
  }

  /**
   * Returns whether the given stock is already in this watchlist.
   *
   * @param stock The stock to look for
   * @return true if the stock is in this watchlist; false otherwise
   */
  public boolean containsStock(Stock stock) {
    return stocks.contains(stock);
  }

  /**
   * Adds a stock to this watchlist if it is not already present.
   *
   * @param stock The stock entity to add
   * @return Whether the stock was added (false if it was already in this watchlist)
   */
  public boolean addStock(Stock stock) {
    if (containsStock(stock)) {
      return false;
    }
    return stocks.add(stock);
  }

  /**
   * Removes a stock from this watchlist.
   *
   * @param stock The stock entity to remove
   * @return Whether the stock was present in this watchlist and removed.
   */
  public boolean removeStock(Stock stock) {
    return stocks.remove(stock);
  }
}
