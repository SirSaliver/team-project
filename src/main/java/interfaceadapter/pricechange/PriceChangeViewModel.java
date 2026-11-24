package interfaceadapter.pricechange;

import interfaceadapter.ViewModel;

/**
 * The view model for the Price Change view.
 */
public class PriceChangeViewModel extends ViewModel<PriceChangeState> {
  /** Fired when a new Set Risk Profile estimate is available to explore. */
  public static final String SCENARIO_PROPERTY = "scenario";
  public static final String RESULT_PROPERTY = "result";
  public static final String ERROR_PROPERTY = "error";

  /**
   * Creates a new PriceChangeViewModel with an empty state.
   */
  public PriceChangeViewModel() {
    super("price change");
    setState(new PriceChangeState());
  }
}
