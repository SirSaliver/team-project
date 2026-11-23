package usecase.addtowatchlist;

/**
 * The DAO interface for the Add To Watchlist Use Case.
 */

public interface WatchlistDataAccessInterface {

    /**
     * Checks if the given TickerSymbol exists.
     * @param tickersymbol the ticker symbol to look for
     * @return true if the ticker symbol exists; false otherwise
     */
    boolean tickersymbolexists(String tickersymbol);

    /**
     * Saves the ticker symbol to the watchlist.
     * @param tickersymbol the ticker symbol to save
     * @param watchlist the watchlist to save to
     */
    void saveToWatchlist(Watchlist watchlist, String tickersymbol);

    /**
     * Returns whether the stock with the given ticker symbol is in the watchlist.
     * @param tickersymbol the ticker symbol to look up
     * @return true is the stock is already in the watchlist; false otherwise
     */
    Watchlist inWatchlist(String tickersymbol);
}

