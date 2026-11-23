package interfaceadapter.watchlist;

import usecase.addtowatchlist.AddToWatchlistInteractor;
import usecase.addtowatchlist.WatchlistInputBoundary;
import usecase.addtowatchlist.WatchlistInputData;

/**
 * The controller for the Add To Watchlist Use Case.
 */
public class WatchlistController {

    private final WatchlistInputBoundary addToWatchlistInteractor;

    public WatchlistController(WatchlistInputBoundary addToWatchlistInteractor) {
        this.addToWatchlistInteractor = addToWatchlistInteractor;
    }

    /**
     * Executes the Add To Watchlist Use Case.
     * @param tickerSymbol the ticker symbol of the stock being added
     */
    public void execute(String tickerSymbol) {
        final WatchlistInputData watchlistInputData = new WatchlistInputData(
                tickerSymbol);

        AddToWatchlistInteractor.execute(watchlistInputData);
    }
}
