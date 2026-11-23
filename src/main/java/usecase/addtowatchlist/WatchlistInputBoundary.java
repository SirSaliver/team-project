package usecase.addtowatchlist;

/**
 * The Add To Watchlist Use Case.
 */
public interface WatchlistInputBoundary {
    /**
     * Execute the Add To Watchlist Use Case.
     * @param watchlistInputData the input data for this use case
     */
    void execute(WatchlistInputData watchlistInputData);

}
