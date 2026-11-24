package app;

import dataaccess.ApiStockDataAccessObject;
import dataaccess.CachedStockHistoryDataAccessObject;
import dataaccess.FileRiskProfileSettingsDataAccessObject;
import dataaccess.FileWatchlistDataAccessObject;
import interfaceadapter.pricechange.PriceChangeController;
import interfaceadapter.pricechange.PriceChangePresenter;
import interfaceadapter.pricechange.PriceChangeViewModel;
import interfaceadapter.setriskprofile.SetRiskProfileController;
import interfaceadapter.setriskprofile.SetRiskProfilePresenter;
import interfaceadapter.setriskprofile.SetRiskProfileViewModel;
import interfaceadapter.viewstockinfo.ViewStockInfoController;
import interfaceadapter.viewstockinfo.ViewStockInfoPresenter;
import interfaceadapter.viewstockinfo.ViewStockInfoViewModel;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import usecase.pricechange.PriceChangeInputBoundary;
import usecase.pricechange.PriceChangeInteractor;
import usecase.setriskprofile.SetRiskProfileInputBoundary;
import usecase.setriskprofile.SetRiskProfileInteractor;
import usecase.shared.projection.SellProjector;
import usecase.shared.projection.TargetGainSellStrategy;
import usecase.viewstockinfo.ViewStockInfoInputBoundary;
import usecase.viewstockinfo.ViewStockInfoInteractor;
import view.PriceChangeView;
import view.SetRiskProfileView;
import view.ViewStockInfoView;

/**
 * Builds the application by wiring together the views, view models, presenters, interactors,
 * and data access objects.
 */
public class AppBuilder {
  private static final String RISK_PROFILE_SETTINGS_FILE = "risk_profile.json";
  private static final String WATCHLIST_FILE = "watchlist.json";

  private final JTabbedPane tabs = new JTabbedPane();

  private final ApiStockDataAccessObject stockDataAccessObject;
  private final FileRiskProfileSettingsDataAccessObject riskProfileSettingsDataAccessObject;

  private SetRiskProfileViewModel setRiskProfileViewModel;
  private SetRiskProfileView setRiskProfileView;
  private SetRiskProfileController setRiskProfileController;

  private PriceChangeViewModel priceChangeViewModel;
  private PriceChangeView priceChangeView;

  /**
   * Creates a new AppBuilder.
   *
   * @param apiKey Massive.com API key
   * @throws IOException If the configuration directory could not be created
   */
  public AppBuilder(String apiKey) throws IOException {
    stockDataAccessObject = new ApiStockDataAccessObject(apiKey);
    riskProfileSettingsDataAccessObject =
            new FileRiskProfileSettingsDataAccessObject(RISK_PROFILE_SETTINGS_FILE);
  }

  /**
   * Adds the Set Risk Profile view to the application.
   *
   * @return This builder
   */
  public AppBuilder addSetRiskProfileView() {
    setRiskProfileViewModel = new SetRiskProfileViewModel();
    setRiskProfileView = new SetRiskProfileView(setRiskProfileViewModel);
    tabs.addTab("Risk Profile", setRiskProfileView);
    return this;
  }

  /**
   * Adds the Price Change view to the application, as an optional next step after a Set Risk
   * Profile estimate. Its tab stays disabled until an estimate is available. Must be called after
   * {@link #addSetRiskProfileView()} and before {@link #addSetRiskProfileUseCase()}.
   *
   * @return This builder
   */
  public AppBuilder addPriceChangeView() {
    priceChangeViewModel = new PriceChangeViewModel();
    priceChangeView = new PriceChangeView(priceChangeViewModel);
    tabs.addTab("Price Change", priceChangeView);
    int tabIndex = tabs.indexOfComponent(priceChangeView);
    tabs.setEnabledAt(tabIndex, false);

    priceChangeViewModel.addPropertyChangeListener(event -> {
      if (PriceChangeViewModel.SCENARIO_PROPERTY.equals(event.getPropertyName())) {
        SwingUtilities.invokeLater(() -> tabs.setEnabledAt(tabIndex, true));
      }
    });
    setRiskProfileView.setPriceChangeNavigation(() -> tabs.setSelectedComponent(priceChangeView));
    return this;
  }

  /**
   * Adds the Price Change use case to the application. Must be called after
   * {@link #addPriceChangeView()}.
   *
   * @return This builder
   */
  public AppBuilder addPriceChangeUseCase() {
    PriceChangePresenter presenter = new PriceChangePresenter(priceChangeViewModel);
    PriceChangeInputBoundary interactor = new PriceChangeInteractor(stockDataAccessObject,
            presenter, new SellProjector(new TargetGainSellStrategy()));
    priceChangeView.setController(new PriceChangeController(interactor));
    return this;
  }

  /**
   * Adds the Set Risk Profile use case to the application. Must be called after
   * {@link #addSetRiskProfileView()}, and after {@link #addPriceChangeView()} if the Price Change
   * use case is included.
   *
   * @return This builder
   */
  public AppBuilder addSetRiskProfileUseCase() {
    // priceChangeViewModel is null unless addPriceChangeView() was called first
    SetRiskProfilePresenter presenter = new SetRiskProfilePresenter(setRiskProfileViewModel,
            priceChangeViewModel);
    SetRiskProfileInputBoundary interactor = new SetRiskProfileInteractor(
            stockDataAccessObject, riskProfileSettingsDataAccessObject, presenter,
            new SellProjector(new TargetGainSellStrategy()));
    setRiskProfileController = new SetRiskProfileController(interactor);
    setRiskProfileView.setController(setRiskProfileController);
    return this;
  }

  /**
   * Adds and wires the Stock Info tab.
   *
   * @return This builder
   * @throws IOException If the watchlist directory cannot be opened
   */
  public AppBuilder addViewStockInfoUseCase() throws IOException {
    ViewStockInfoViewModel viewModel = new ViewStockInfoViewModel();
    ViewStockInfoPresenter presenter = new ViewStockInfoPresenter(viewModel);
    ViewStockInfoInputBoundary interactor = new ViewStockInfoInteractor(
            new CachedStockHistoryDataAccessObject(stockDataAccessObject),
            new FileWatchlistDataAccessObject(WATCHLIST_FILE), presenter);
    ViewStockInfoController controller = new ViewStockInfoController(interactor);
    ViewStockInfoView view = new ViewStockInfoView(viewModel, controller);
    tabs.addChangeListener(event -> {
      if (tabs.getSelectedComponent() == view) {
        controller.loadWatchlist();
      }
    });
    tabs.addTab("Stock Info", view);
    return this;
  }

  /**
   * Builds the application window.
   *
   * @return The application's main frame
   */
  public JFrame build() {
    JFrame application = new JFrame("Stock Tracker");
    application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    application.add(tabs);

    if (setRiskProfileController != null) {
      // Restore the user's saved risk profile settings from the previous run.
      setRiskProfileController.loadSettings();
      tabs.setSelectedComponent(setRiskProfileView);
    }
    application.pack();
    application.setLocationRelativeTo(null);
    return application;
  }
}
