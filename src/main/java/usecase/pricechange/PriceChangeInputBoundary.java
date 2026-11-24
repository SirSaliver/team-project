package usecase.pricechange;

/**
 * The Price Change use case.
 */
public interface PriceChangeInputBoundary {
  /**
   * Execute the Price Change use case.
   *
   * @param inputData The input data for this use case
   */
  void execute(PriceChangeInputData inputData);
}
