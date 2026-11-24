package usecase.addtowatchlist;

/**
 * A stock returned by a search, as described by the market data provider.
 */
public class StockSearchResult {
  private final String ticker;
  private final String companyName;
  private final String exchange;

  /**
   * Creates a search result.
   *
   * @param ticker The stock's ticker symbol
   * @param companyName The company's name, or null if the provider did not supply one
   * @param exchange The stock's primary exchange code, or null if the provider did not supply one
   */
  public StockSearchResult(String ticker, String companyName, String exchange) {
    this.ticker = ticker;
    this.companyName = companyName;
    this.exchange = exchange;
  }

  public String getTicker() {
    return ticker;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getExchange() {
    return exchange;
  }
}
