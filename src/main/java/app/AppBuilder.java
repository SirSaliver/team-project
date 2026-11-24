package app;

import dataaccess.ApiStockDataAccessObject;
import dataaccess.CachedStockHistoryDataAccessObject;
import dataaccess.FileRiskProfileSettingsDataAccessObject;
import dataaccess.FileWatchlistDataAccessObject;
import interfaceadapter.addtowatchlist.AddToWatchlistController;
import interfaceadapter.addtowatchlist.AddToWatchlistPresenter;
import interfaceadapter.addtowatchlist.AddToWatchlistViewModel;
import interfaceadapter.setriskprofile.SetRiskProfileController;
import interfaceadapter.setriskprofile.SetRiskProfilePresenter;
import interfaceadapter.setriskprofile.SetRiskProfileViewModel;
import interfaceadapter.viewstockinfo.ViewStockInfoController;
import interfaceadapter.viewstockinfo.ViewStockInfoPresenter;
import interfaceadapter.viewstockinfo.ViewStockInfoViewModel;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import usecase.addtowatchlist.AddToWatchlistInputBoundary;
import usecase.addtowatchlist.AddToWatchlistInteractor;
import usecase.setriskprofile.SetRiskProfileInputBoundary;
import usecase.setriskprofile.SetRiskProfileInteractor;
import usecase.shared.projection.TargetGainSellStrategy;
import usecase.viewstockinfo.ViewStockInfoInputBoundary;
import usecase.viewstockinfo.ViewStockInfoInteractor;
import view.AddToWatchlistView;
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
  private final FileWatchlistDataAccessObject watchlistDataAccessObject;

  private SetRiskProfileViewModel setRiskProfileViewModel;
  private SetRiskProfileView setRiskProfileView;
  private SetRiskProfileController setRiskProfileController;

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
    watchlistDataAccessObject = new FileWatchlistDataAccessObject(WATCHLIST_FILE);
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
   * Adds the Set Risk Profile use case to the application. Must be called after
   * {@link #addSetRiskProfileView()}.
   *
   * @return This builder
   */
  public AppBuilder addSetRiskProfileUseCase() {
    SetRiskProfilePresenter presenter = new SetRiskProfilePresenter(setRiskProfileViewModel);
    SetRiskProfileInputBoundary interactor = new SetRiskProfileInteractor(
            stockDataAccessObject, riskProfileSettingsDataAccessObject, presenter,
            new TargetGainSellStrategy());
    setRiskProfileController = new SetRiskProfileController(interactor);
    setRiskProfileView.setController(setRiskProfileController);
    return this;
  }

  /**
   * Adds and wires the Add Stocks tab, where the user searches for stocks to add to the watchlist.
   *
   * @return This builder
   */
  public AppBuilder addAddToWatchlistUseCase() {
    AddToWatchlistViewModel viewModel = new AddToWatchlistViewModel();
    AddToWatchlistPresenter presenter = new AddToWatchlistPresenter(viewModel);
    AddToWatchlistInputBoundary interactor = new AddToWatchlistInteractor(
            watchlistDataAccessObject, stockDataAccessObject, presenter);
    AddToWatchlistController controller = new AddToWatchlistController(interactor);
    tabs.addTab("Add Stocks", new AddToWatchlistView(viewModel, controller));
    return this;
  }

  /**
   * Adds and wires the Stock Info tab.
   *
   * @return This builder
   */
  public AppBuilder addViewStockInfoUseCase() {
    ViewStockInfoViewModel viewModel = new ViewStockInfoViewModel();
    ViewStockInfoPresenter presenter = new ViewStockInfoPresenter(viewModel);
    ViewStockInfoInputBoundary interactor = new ViewStockInfoInteractor(
            new CachedStockHistoryDataAccessObject(stockDataAccessObject),
            watchlistDataAccessObject, presenter);
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
