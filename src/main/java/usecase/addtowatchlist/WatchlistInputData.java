package usecase.addtowatchlist;
/**
 * The input data for the Add To Watchlist Use Case.
 */
public class WatchlistInputData {
    private final String tickerSymbol;

    public WatchlistInputData(String tickerSymbol){

        this.tickerSymbol = tickerSymbol;
    }

    String getTickerSymbol() {
        return tickerSymbol;
    }
}
