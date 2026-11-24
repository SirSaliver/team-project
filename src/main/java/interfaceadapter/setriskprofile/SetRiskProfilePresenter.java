package interfaceadapter.setriskprofile;

import interfaceadapter.pricechange.PriceChangeState;
import interfaceadapter.pricechange.PriceChangeViewModel;
import interfaceadapter.shared.ProjectionFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import usecase.setriskprofile.SetRiskProfileOutputBoundary;
import usecase.setriskprofile.SetRiskProfileOutputData;
import usecase.setriskprofile.SetRiskProfileSettingsOutputData;
import usecase.shared.projection.SellEstimate;
import usecase.shared.projection.SellProjection;

/**
 * The presenter for the Set Risk Profile use case.
 */
public class SetRiskProfilePresenter implements SetRiskProfileOutputBoundary {
  private static final int TRADING_DAYS_PER_MONTH = 21;

  private final SetRiskProfileViewModel viewModel;
  private final PriceChangeViewModel priceChangeViewModel;

  /**
   * Creates a new Set Risk Profile presenter that also hands each successful estimate to the
   * Price Change view, so the user can explore a price change scenario as a next step.
   *
   * @param viewModel View model for the use case
   * @param priceChangeViewModel View model for the Price Change use case, or null if the
   *                             application does not include it
   */
  public SetRiskProfilePresenter(SetRiskProfileViewModel viewModel,
                                 PriceChangeViewModel priceChangeViewModel) {
    this.viewModel = viewModel;
    this.priceChangeViewModel = priceChangeViewModel;
  }

  @Override
  public void prepareSuccessView(SetRiskProfileOutputData outputData) {
    SetRiskProfileState state = viewModel.getState();
    SellProjection projection = outputData.getProjection();
    SellEstimate estimate = projection.getEstimate();

    double sellPrice = estimate.getSellPrice();

    state.setResultTicker(outputData.getTicker());
    state.setBuyPrice(ProjectionFormat.formatBuyPrice(projection.getStartPrice()));
    state.setSellDate(ProjectionFormat.formatSellDate(projection.getSellDate(),
            estimate.getHoldPeriod()));
    state.setSellPrice(ProjectionFormat.formatDollars(sellPrice));

    state.setPriceRange(ProjectionFormat.formatPriceRange(projection.getSellPriceLow(),
            projection.getSellPriceHigh()));
    state.setReturns(ProjectionFormat.formatDollarsSigned(estimate.getReturns()));
    state.setTargetReached(estimate.isTargetReached());
    state.setTargetMessage(ProjectionFormat.formatTargetReached(estimate.isTargetReached()));
    state.setErrorMessage(null);

    viewModel.setState(state);
    viewModel.firePropertyChange(SetRiskProfileViewModel.RESULT_PROPERTY);

    if (priceChangeViewModel != null) {
      prepareScenario(outputData);
    }
  }

  /**
   * Makes this estimate the starting point of the Price Change use case.
   *
   * @param outputData Output data of the successful estimate
   */
  private void prepareScenario(SetRiskProfileOutputData outputData) {
    double targetGainPercent = outputData.getTargetGain() * 100.0;
    PriceChangeState state = priceChangeViewModel.getState();
    state.setScenarioAvailable(true);
    state.setScenarioTicker(outputData.getTicker());
    state.setScenarioAmount(outputData.getAmount());
    state.setScenarioTargetGainPercent(targetGainPercent);
    state.setScenarioMaxHoldingPeriod(outputData.getMaxHoldingPeriod());
    state.setScenarioWindowMonths(outputData.getWindowMonths());
    state.setScenarioSummary(outputData.getTicker() + " · "
            + ProjectionFormat.formatDollars(outputData.getAmount()) + " invested · "
            + formatPercent(targetGainPercent) + "% target gain · up to "
            + outputData.getMaxHoldingPeriod() + " trading days · estimated from the past "
            + SetRiskProfileViewModel.formatMonths(outputData.getWindowMonths()));
    state.setErrorMessage(null);

    priceChangeViewModel.setState(state);
    priceChangeViewModel.firePropertyChange(PriceChangeViewModel.SCENARIO_PROPERTY);
  }

  @Override
  public void prepareFailView(String errorMessage) {
    SetRiskProfileState state = viewModel.getState();
    state.setErrorMessage(errorMessage);
    viewModel.setState(state);
    viewModel.firePropertyChange(SetRiskProfileViewModel.ERROR_PROPERTY);
  }

  @Override
  public void prepareSettingsView(SetRiskProfileSettingsOutputData outputData) {
    SetRiskProfileState state = viewModel.getState();

    List<SetRiskProfileState.PresetOption> options = new ArrayList<>();
    int selectedIndex = -1;
    List<SetRiskProfileSettingsOutputData.Preset> presets = outputData.getPresets();
    for (int i = 0; i < presets.size(); i++) {
      SetRiskProfileSettingsOutputData.Preset preset = presets.get(i);
      double gainPercent = preset.getTargetGain() * 100;
      String label = String.format("%s (+%s%%, %s)", capitalize(preset.getName()),
              formatPercent(gainPercent), formatTradingDays(preset.getMaxHoldingPeriod()));
      options.add(new SetRiskProfileState.PresetOption(label, gainPercent,
              preset.getMaxHoldingPeriod()));
      if (preset.getName().equals(outputData.getSelectedPreset())) {
        selectedIndex = i;
      }
    }

    state.setPresets(options);
    state.setSelectedPresetIndex(selectedIndex);
    state.setTargetGainPercent(outputData.getTargetGain() * 100);
    state.setMaxHoldingPeriod(outputData.getMaxHoldingPeriod());
    state.setWindowMonths(outputData.getWindow());

    viewModel.setState(state);
    viewModel.firePropertyChange(SetRiskProfileViewModel.SETTINGS_PROPERTY);
  }

  private static String formatPercent(double percent) {
    if (percent == Math.rint(percent)) {
      return String.valueOf((long) percent);
    }
    return String.format(Locale.US, "%.2f", percent);
  }

  private static String capitalize(String name) {
    String lower = name.toLowerCase(Locale.ROOT);
    return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
  }

  private static String formatTradingDays(int tradingDays) {
    if (tradingDays % TRADING_DAYS_PER_MONTH == 0) {
      return SetRiskProfileViewModel.formatMonths(tradingDays / TRADING_DAYS_PER_MONTH);
    }
    return tradingDays + " trading days";
  }
}
