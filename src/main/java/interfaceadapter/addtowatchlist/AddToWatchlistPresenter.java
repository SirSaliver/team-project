package interfaceadapter.addtowatchlist;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import usecase.addtowatchlist.AddToWatchlistOutputBoundary;
import usecase.addtowatchlist.AddToWatchlistOutputData;
import usecase.addtowatchlist.AddToWatchlistSearchOutputData;
import usecase.addtowatchlist.StockSearchResult;

/**
 * The Presenter for the Add To Watchlist Use Case.
 */
public class AddToWatchlistPresenter implements AddToWatchlistOutputBoundary {
  private static final String MISSING = "—";
  /** Display names for the exchange codes (ISO 10383 MICs) that the provider returns. */
  private static final Map<String, String> EXCHANGE_NAMES = Map.of(
          "XNAS", "NASDAQ",
          "XNYS", "NYSE",
          "ARCX", "NYSE Arca",
          "XASE", "NYSE American",
          "BATS", "Cboe BZX");

  private final AddToWatchlistViewModel viewModel;

  /**
   * Creates a presenter for search and add results.
   *
   * @param viewModel The view model to update
   */
  public AddToWatchlistPresenter(AddToWatchlistViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public synchronized void prepareSearchSuccessView(AddToWatchlistSearchOutputData outputData) {
    List<AddToWatchlistState.Row> rows = outputData.getResults().stream()
            .map(AddToWatchlistPresenter::toRow)
            .collect(Collectors.toList());
    int count = rows.size();
    String message = count + (count == 1 ? " stock matches" : " stocks match")
            + " \"" + outputData.getQuery() + "\".";
    update(rows, currentWatchlistTickers(), message, false,
            AddToWatchlistViewModel.SEARCH_PROPERTY);
  }

  @Override
  public synchronized void prepareSearchFailView(String errorMessage) {
    update(List.of(), currentWatchlistTickers(), errorMessage, true,
            AddToWatchlistViewModel.SEARCH_PROPERTY);
  }

  @Override
  public synchronized void prepareAddSuccessView(AddToWatchlistOutputData outputData) {
    update(viewModel.getState().getRows(), Set.copyOf(outputData.getWatchlistTickers()),
            outputData.getAddedTicker() + " was added to your watchlist.", false,
            AddToWatchlistViewModel.ADD_PROPERTY);
  }

  @Override
  public synchronized void prepareAddFailView(String errorMessage) {
    update(viewModel.getState().getRows(), currentWatchlistTickers(), errorMessage, true,
            AddToWatchlistViewModel.ADD_PROPERTY);
  }

  private Set<String> currentWatchlistTickers() {
    return viewModel.getState().getWatchlistTickers();
  }

  private void update(List<AddToWatchlistState.Row> rows, Set<String> watchlistTickers,
                      String message, boolean error, String property) {
    viewModel.setState(new AddToWatchlistState(rows, watchlistTickers, message, error));
    viewModel.firePropertyChange(property);
  }

  private static AddToWatchlistState.Row toRow(StockSearchResult result) {
    String name = result.getCompanyName();
    String exchange = result.getExchange();
    return new AddToWatchlistState.Row(result.getTicker(),
            name == null || name.isBlank() ? MISSING : name,
            exchange == null || exchange.isBlank() ? MISSING
                    : EXCHANGE_NAMES.getOrDefault(exchange, exchange));
  }
}
