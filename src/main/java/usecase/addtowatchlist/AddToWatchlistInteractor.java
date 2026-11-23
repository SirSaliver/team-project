package usecase.addtowatchlist;

/**
 * Interactor.
 */
public class AddToWatchlistInteractor implements WatchlistInputBoundary {
    private final WatchlistDataAccessInterface watchlistDataAccessObject;
    private final WatchlistOutputBoundary watchlistPresenter;

    public AddToWatchlistInteractor(WatchlistDataAccessInterface watchlistDataAccessInterface,
                                    WatchlistOutputBoundary watchlistOutputBoundary){
        this.watchlistDataAccessObject = watchlistDataAccessInterface;
        this.watchlistPresenter = watchlistOutputBoundary;
    }

    @Override
    public void execute(WatchlistInputData watchlistInputData){
        if ("".equals(watchlistInputData.getTickerSymbol())){
            watchlistPresenter.prepareFailView("Empty search is not valid");
        }


        }

    }
}


