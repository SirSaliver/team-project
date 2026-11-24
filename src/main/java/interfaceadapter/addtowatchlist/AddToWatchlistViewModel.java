package interfaceadapter.addtowatchlist;

import interfaceadapter.ViewModel;
import java.util.List;
import java.util.Set;

/** Stores the search results and status for the Add To Watchlist view. */
public class AddToWatchlistViewModel extends ViewModel<AddToWatchlistState> {
  /** Fired when the search results change. */
  public static final String SEARCH_PROPERTY = "search";
  /** Fired when an add attempt finishes. */
  public static final String ADD_PROPERTY = "add";

  /** Creates the view model with no search results. */
  public AddToWatchlistViewModel() {
    super("add to watchlist");
    setState(new AddToWatchlistState(List.of(), Set.of(), null, false));
  }
}
