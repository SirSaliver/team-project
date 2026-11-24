package view;

import interfaceadapter.addtowatchlist.AddToWatchlistController;
import interfaceadapter.addtowatchlist.AddToWatchlistState;
import interfaceadapter.addtowatchlist.AddToWatchlistViewModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/**
 * The Add To Watchlist View: search for stocks by ticker or company name and add one to the
 * watchlist.
 */
public class AddToWatchlistView extends JPanel {
  private final AddToWatchlistController controller;
  private final JTextField searchField = new JTextField(24);
  private final JButton searchButton = new JButton("Search");
  private final JButton addButton = new JButton("Add to watchlist");
  private final JLabel statusLabel = new JLabel(" ");
  private final DefaultTableModel tableModel = new DefaultTableModel(
          new String[] {"Symbol", "Company", "Exchange"}, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
  };
  private final JTable resultsTable = new JTable(tableModel);
  private AddToWatchlistState state;
  private boolean requestInProgress;

  /**
   * Creates the view and connects it to its view model and controller.
   *
   * @param viewModel The view model holding the search results and status
   * @param controller The controller used to search for and add stocks
   */
  public AddToWatchlistView(AddToWatchlistViewModel viewModel,
                            AddToWatchlistController controller) {
    this.controller = controller;
    this.state = viewModel.getState();
    viewModel.addPropertyChangeListener(this::onStateChanged);

    setLayout(new BorderLayout(0, 12));
    setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

    JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 0));
    JLabel searchLabel = new JLabel("Ticker symbol or company name:");
    searchLabel.setLabelFor(searchField);
    searchRow.add(searchLabel);
    searchRow.add(searchField);
    searchRow.add(searchButton);
    searchField.addActionListener(event -> onSearch());
    searchButton.addActionListener(event -> onSearch());

    JPanel north = new JPanel(new BorderLayout(0, 8));
    north.add(searchRow, BorderLayout.NORTH);
    north.add(statusLabel, BorderLayout.SOUTH);
    add(north, BorderLayout.NORTH);

    resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    resultsTable.setFillsViewportHeight(true);
    resultsTable.getSelectionModel().addListSelectionListener(event -> updateAddButton());
    resultsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
    resultsTable.getColumnModel().getColumn(1).setPreferredWidth(400);
    resultsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
    JScrollPane resultsPane = new JScrollPane(resultsTable);
    resultsPane.setPreferredSize(new Dimension(640, 320));
    resultsPane.setBorder(BorderFactory.createTitledBorder("Search results"));
    add(resultsPane, BorderLayout.CENTER);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
    actions.add(addButton);
    addButton.addActionListener(event -> onAdd());
    add(actions, BorderLayout.SOUTH);
    updateAddButton();
  }

  private void onSearch() {
    if (requestInProgress) {
      return;
    }
    String query = searchField.getText();
    setStatus("Searching…", false);
    runInBackground(() -> controller.search(query), "search");
  }

  private void onAdd() {
    String ticker = selectedTicker();
    if (requestInProgress || ticker == null) {
      return;
    }
    setStatus("Adding " + ticker + "…", false);
    runInBackground(() -> controller.addToWatchlist(ticker), "add");
  }

  /** Runs a controller call off the event dispatch thread, since it may use the network. */
  private void runInBackground(Runnable request, String actionName) {
    setRequestInProgress(true);
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() {
        request.run();
        return null;
      }

      @Override
      protected void done() {
        try {
          get();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          setStatus("The " + actionName + " was interrupted.", true);
        } catch (ExecutionException e) {
          setStatus("An unexpected error prevented the " + actionName + ".", true);
        } finally {
          setRequestInProgress(false);
        }
      }
    }.execute();
  }

  private void onStateChanged(PropertyChangeEvent event) {
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(() -> onStateChanged(event));
      return;
    }

    state = (AddToWatchlistState) event.getNewValue();
    if (AddToWatchlistViewModel.SEARCH_PROPERTY.equals(event.getPropertyName())) {
      showRows(state.getRows());
    }
    setStatus(state.getMessage(), state.isError());
    updateAddButton();
  }

  private void showRows(List<AddToWatchlistState.Row> rows) {
    tableModel.setRowCount(0);
    for (AddToWatchlistState.Row row : rows) {
      tableModel.addRow(new Object[] {row.getTicker(), row.getCompanyName(), row.getExchange()});
    }
    if (!rows.isEmpty()) {
      resultsTable.setRowSelectionInterval(0, 0);
    }
  }

  private String selectedTicker() {
    int row = resultsTable.getSelectedRow();
    return row < 0 ? null : (String) tableModel.getValueAt(row, 0);
  }

  private void setRequestInProgress(boolean inProgress) {
    requestInProgress = inProgress;
    searchButton.setEnabled(!inProgress);
    searchField.setEnabled(!inProgress);
    updateAddButton();
  }

  private void updateAddButton() {
    String ticker = selectedTicker();
    boolean alreadyAdded = ticker != null && state.isInWatchlist(ticker);
    addButton.setEnabled(!requestInProgress && ticker != null && !alreadyAdded);
    addButton.setText(alreadyAdded ? "Already in watchlist" : "Add to watchlist");
  }

  private void setStatus(String message, boolean error) {
    statusLabel.setForeground(error ? Color.RED.darker() : UIManager.getColor("Label.foreground"));
    statusLabel.setText(message == null || message.isEmpty() ? " " : message);
  }
}
