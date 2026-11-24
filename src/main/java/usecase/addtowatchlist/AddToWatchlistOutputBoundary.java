package usecase.addtowatchlist;

/**
 * The output boundary for the Add To Watchlist Use Case.
 */
public interface AddToWatchlistOutputBoundary {
  /**
   * Prepares the view showing the stocks that matched a search.
   *
   * @param outputData The query and its nonempty results
   */
  void prepareSearchSuccessView(AddToWatchlistSearchOutputData outputData);

  /**
   * Prepares the view when a search produced no results to show.
   *
   * @param errorMessage Explanation of why no results are shown
   */
  void prepareSearchFailView(String errorMessage);

  /**
   * Prepares the view confirming that a stock was added.
   *
   * @param outputData The added ticker and the updated watchlist
   */
  void prepareAddSuccessView(AddToWatchlistOutputData outputData);

  /**
   * Prepares the view when a stock could not be added.
   *
   * @param errorMessage Explanation of the failure
   */
  void prepareAddFailView(String errorMessage);
}
