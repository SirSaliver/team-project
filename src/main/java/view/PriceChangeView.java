package view;

import interfaceadapter.pricechange.PriceChangeController;
import interfaceadapter.pricechange.PriceChangeState;
import interfaceadapter.pricechange.PriceChangeViewModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

/**
 * The view for the Price Change use case. Builds on the most recent Set Risk Profile estimate:
 * the user enters a hypothetical price change, and the view shows whether selling right away or
 * continuing to hold is expected to return more.
 */
public class PriceChangeView extends JPanel implements PropertyChangeListener {
  private static final String FETCHING_MESSAGE = "Fetching price data…";
  private static final String NO_SCENARIO_MESSAGE =
          "Run an estimate on the Risk Profile tab first, then explore a price change here.";

  private final PriceChangeViewModel viewModel;
  private PriceChangeController controller;

  private final JLabel scenarioLabel = new JLabel(NO_SCENARIO_MESSAGE);
  private final JSpinner priceChangeSpinner =
          new JSpinner(new SpinnerNumberModel(-10.0, -99.0, 1000.0, 1.0));
  private final JButton exploreButton = new JButton("Explore");

  private final JLabel statusLabel = new JLabel(" ");
  private final JPanel resultPanel = new JPanel(new GridBagLayout());
  private final JLabel resultTitleLabel = new JLabel();
  private final JLabel buyPriceLabel = new JLabel();
  private final JLabel changedPriceLabel = new JLabel();
  private final JLabel sellNowReturnsLabel = new JLabel();
  private final JLabel sellDateLabel = new JLabel();
  private final JLabel sellPriceLabel = new JLabel();
  private final JLabel priceRangeLabel = new JLabel();
  private final JLabel returnsLabel = new JLabel();
  private final JLabel targetMessageLabel = new JLabel();
  private final JLabel recommendationLabel = new JLabel();

  /**
   * Creates the Price Change view.
   *
   * @param viewModel The view model this view displays
   */
  public PriceChangeView(PriceChangeViewModel viewModel) {
    this.viewModel = viewModel;
    viewModel.addPropertyChangeListener(this);

    setLayout(new BorderLayout(0, 12));
    setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

    JLabel title = new JLabel("Price Change Scenario");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
    add(title, BorderLayout.NORTH);

    add(buildForm(), BorderLayout.CENTER);
    add(buildResults(), BorderLayout.SOUTH);

    priceChangeSpinner.setEditor(new JSpinner.NumberEditor(priceChangeSpinner, "+0.##;-0.##"));
    exploreButton.addActionListener(e -> onExplore());
    setScenarioEnabled(false);
  }

  public String getViewName() {
    return viewModel.getViewName();
  }

  public void setController(PriceChangeController controller) {
    this.controller = controller;
  }

  private JPanel buildForm() {
    final JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets(4, 4, 8, 4);
    form.add(scenarioLabel, c);

    int row = 1;
    addRow(form, row++, "Price change today (%):", priceChangeSpinner);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = row++;
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets(8, 4, 4, 4);
    form.add(exploreButton, c);

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = row;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets(4, 4, 4, 4);
    form.add(statusLabel, c);
    return form;
  }

  private JPanel buildResults() {
    resultPanel.setBorder(BorderFactory.createTitledBorder("Sell now or hold?"));
    resultTitleLabel.setFont(resultTitleLabel.getFont().deriveFont(Font.BOLD));
    recommendationLabel.setFont(recommendationLabel.getFont().deriveFont(Font.BOLD));
    addWideRow(resultPanel, 0, resultTitleLabel);

    int row = 1;
    addRow(resultPanel, row++, "Buy price:", buyPriceLabel);
    addRow(resultPanel, row++, "Price after change:", changedPriceLabel);
    addRow(resultPanel, row++, "Return if sold now:", sellNowReturnsLabel);
    addRow(resultPanel, row++, "If held, sell on:", sellDateLabel);
    addRow(resultPanel, row++, "Projected sell price:", sellPriceLabel);
    addRow(resultPanel, row++, "Likely price range (±1σ):", priceRangeLabel);
    addRow(resultPanel, row++, "Projected return if held:", returnsLabel);
    addWideRow(resultPanel, row++, targetMessageLabel);
    addWideRow(resultPanel, row, recommendationLabel);

    resultPanel.setVisible(false);
    return resultPanel;
  }

  private static void addRow(JPanel panel, int row, String label, JComponent field) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = row;
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets(4, 4, 4, 12);
    panel.add(new JLabel(label), c);

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = row;
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    c.insets = new Insets(4, 4, 4, 4);
    panel.add(field, c);
  }

  private static void addWideRow(JPanel panel, int row, JComponent component) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = row;
    c.gridwidth = 2;
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets(4, 4, 4, 4);
    panel.add(component, c);
  }

  private void onExplore() {
    PriceChangeState state = viewModel.getState();
    if (controller == null || !exploreButton.isEnabled() || !state.isScenarioAvailable()) {
      return;
    }
    try {
      priceChangeSpinner.commitEdit();
    } catch (ParseException e) {
      // Invalid text is discarded and the spinner keeps its last valid value.
    }

    String ticker = state.getScenarioTicker();
    double amount = state.getScenarioAmount();
    double targetGainPercent = state.getScenarioTargetGainPercent();
    int maxHoldingPeriod = state.getScenarioMaxHoldingPeriod();
    int windowMonths = state.getScenarioWindowMonths();
    double priceChangePercent = ((Number) priceChangeSpinner.getValue()).doubleValue();

    exploreButton.setEnabled(false);
    setStatus(FETCHING_MESSAGE, false);

    // Run the use case off the Event Dispatch Thread so the UI stays responsive during the
    // network call.
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() {
        controller.execute(ticker, amount, targetGainPercent, maxHoldingPeriod, windowMonths,
                priceChangePercent);
        return null;
      }

      @Override
      protected void done() {
        exploreButton.setEnabled(true);
        if (FETCHING_MESSAGE.equals(statusLabel.getText())) {
          setStatus(" ", false);
        }
        try {
          get();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
          setStatus("Unexpected error: " + e.getCause().getMessage(), true);
        }
      }
    }.execute();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // The presenter may update the view model from a background thread; Swing components must
    // only be touched on the Event Dispatch Thread.
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(() -> propertyChange(evt));
      return;
    }

    PriceChangeState state = (PriceChangeState) evt.getNewValue();
    switch (evt.getPropertyName()) {
      case PriceChangeViewModel.SCENARIO_PROPERTY:
        showScenario(state);
        break;
      case PriceChangeViewModel.RESULT_PROPERTY:
        showResult(state);
        break;
      case PriceChangeViewModel.ERROR_PROPERTY:
        setStatus(state.getErrorMessage(), true);
        break;
      default:
        break;
    }
  }

  private void showScenario(PriceChangeState state) {
    scenarioLabel.setText("Based on your estimate: " + state.getScenarioSummary());
    setScenarioEnabled(true);
    // A result from an earlier estimate no longer applies
    resultPanel.setVisible(false);
    setStatus(" ", false);
  }

  private void showResult(PriceChangeState state) {
    resultTitleLabel.setText("Scenario for " + state.getResultTicker());
    buyPriceLabel.setText(state.getBuyPrice());
    changedPriceLabel.setText(state.getChangedPrice());
    sellNowReturnsLabel.setText(state.getSellNowReturns());
    sellDateLabel.setText(state.getSellDate());
    sellPriceLabel.setText(state.getSellPrice());
    priceRangeLabel.setText(state.getPriceRange());
    returnsLabel.setText(state.getReturns());
    targetMessageLabel.setText(state.getTargetMessage());
    targetMessageLabel.setForeground(state.isTargetReached()
            ? new Color(0, 128, 0) : new Color(176, 112, 0));
    recommendationLabel.setText(state.getRecommendation());
    resultPanel.setVisible(true);
    setStatus(" ", false);
    revalidateWindow();
  }

  private void setScenarioEnabled(boolean enabled) {
    priceChangeSpinner.setEnabled(enabled);
    exploreButton.setEnabled(enabled);
  }

  private void setStatus(String message, boolean isError) {
    statusLabel.setText(message == null || message.isEmpty() ? " " : message);
    statusLabel.setForeground(isError ? Color.RED : UIManager.getColor("Label.foreground"));
    revalidateWindow();
  }

  private void revalidateWindow() {
    revalidate();
    Window window = SwingUtilities.getWindowAncestor(this);
    if (window != null) {
      window.pack();
    }
  }
}
