package usecase.pricechange;

/**
 * Output boundary for the Price Change use case.
 */
public interface PriceChangeOutputBoundary {
  /**
   * Prepares the success view for the Price Change use case.
   *
   * @param outputData The output data
   */
  void prepareSuccessView(PriceChangeOutputData outputData);

  /**
   * Prepares the failure view for the Price Change use case.
   *
   * @param errorMessage Explanation of the failure
   */
  void prepareFailView(String errorMessage);
}
