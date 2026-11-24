package interfaceadapter.pricechange;

import interfaceadapter.shared.ProjectionFormat;
import usecase.pricechange.PriceChangeOutputBoundary;
import usecase.pricechange.PriceChangeOutputData;
import usecase.shared.projection.SellEstimate;
import usecase.shared.projection.SellProjection;

/**
 * The presenter for the Price Change use case.
 */
public class PriceChangePresenter implements PriceChangeOutputBoundary {
  private final PriceChangeViewModel viewModel;

  /**
   * Creates a new Price Change presenter.
   *
   * @param viewModel View model for the use case
   */
  public PriceChangePresenter(PriceChangeViewModel viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void prepareSuccessView(PriceChangeOutputData outputData) {
    PriceChangeState state = viewModel.getState();
    SellProjection projection = outputData.getProjection();
    SellEstimate estimate = projection.getEstimate();

    state.setResultTicker(outputData.getTicker());
    state.setBuyPrice(ProjectionFormat.formatBuyPrice(projection.getStartPrice()));
    state.setChangedPrice(ProjectionFormat.formatDollars(projection.getCurrentPrice()));
    state.setSellDate(ProjectionFormat.formatSellDate(projection.getSellDate(),
            estimate.getHoldPeriod()));
    state.setSellPrice(ProjectionFormat.formatDollars(estimate.getSellPrice()));
    state.setSellNowReturns(ProjectionFormat.formatDollarsSigned(outputData.getSellNowResult()));

    state.setPriceRange(ProjectionFormat.formatPriceRange(projection.getSellPriceLow(),
            projection.getSellPriceHigh()));
    state.setReturns(ProjectionFormat.formatDollarsSigned(estimate.getReturns()));
    state.setTargetReached(estimate.isTargetReached());
    state.setTargetMessage(ProjectionFormat.formatTargetReached(estimate.isTargetReached()));
    state.setRecommendation(formatRecommendation(outputData));
    state.setErrorMessage(null);

    viewModel.setState(state);
    viewModel.firePropertyChange(PriceChangeViewModel.RESULT_PROPERTY);
  }

  @Override
  public void prepareFailView(String errorMessage) {
    PriceChangeState state = viewModel.getState();
    state.setErrorMessage(errorMessage);
    viewModel.setState(state);
    viewModel.firePropertyChange(PriceChangeViewModel.ERROR_PROPERTY);
  }

  private static String formatRecommendation(PriceChangeOutputData outputData) {
    SellEstimate estimate = outputData.getProjection().getEstimate();
    if (estimate.isTargetReached() && estimate.getHoldPeriod() == 0) {
      return "Sell now; your target gain is already reached at this price.";
    }
    if (!outputData.isHoldingBetter()) {
      return "Selling now would provide a higher return than holding.";
    }
    if (outputData.isHoldingBetterAtLow()) {
      return "Holding is expected to beat selling now, even at the low end of the price range.";
    }
    return "Holding is expected to beat selling now, but not at the low end of the price range.";
  }
}
