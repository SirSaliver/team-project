package interfaceadapter.pricechange;

/**
 * The state shown by the Price Change view. The scenario fields hold the inputs of the most
 * recent Set Risk Profile estimate, which this use case builds on; all other values are already
 * formatted for display.
 */
public class PriceChangeState {
  private boolean scenarioAvailable;
  private String scenarioTicker = "";
  private double scenarioAmount;
  private double scenarioTargetGainPercent;
  private int scenarioMaxHoldingPeriod;
  private int scenarioWindowMonths;
  private String scenarioSummary = "";

  private String resultTicker = "";
  private String buyPrice = "";
  private String changedPrice = "";
  private String sellDate = "";
  private String sellPrice = "";
  private String priceRange = "";
  private String returns = "";
  private String targetMessage = "";
  private String sellNowReturns = "";
  private String recommendation = "";
  private boolean targetReached;

  private String errorMessage;

  public boolean isScenarioAvailable() {
    return scenarioAvailable;
  }

  public void setScenarioAvailable(boolean scenarioAvailable) {
    this.scenarioAvailable = scenarioAvailable;
  }

  public String getScenarioTicker() {
    return scenarioTicker;
  }

  public void setScenarioTicker(String scenarioTicker) {
    this.scenarioTicker = scenarioTicker;
  }

  public double getScenarioAmount() {
    return scenarioAmount;
  }

  public void setScenarioAmount(double scenarioAmount) {
    this.scenarioAmount = scenarioAmount;
  }

  public double getScenarioTargetGainPercent() {
    return scenarioTargetGainPercent;
  }

  public void setScenarioTargetGainPercent(double scenarioTargetGainPercent) {
    this.scenarioTargetGainPercent = scenarioTargetGainPercent;
  }

  public int getScenarioMaxHoldingPeriod() {
    return scenarioMaxHoldingPeriod;
  }

  public void setScenarioMaxHoldingPeriod(int scenarioMaxHoldingPeriod) {
    this.scenarioMaxHoldingPeriod = scenarioMaxHoldingPeriod;
  }

  public int getScenarioWindowMonths() {
    return scenarioWindowMonths;
  }

  public void setScenarioWindowMonths(int scenarioWindowMonths) {
    this.scenarioWindowMonths = scenarioWindowMonths;
  }

  public String getScenarioSummary() {
    return scenarioSummary;
  }

  public void setScenarioSummary(String scenarioSummary) {
    this.scenarioSummary = scenarioSummary;
  }

  public String getResultTicker() {
    return resultTicker;
  }

  public void setResultTicker(String resultTicker) {
    this.resultTicker = resultTicker;
  }

  public String getBuyPrice() {
    return buyPrice;
  }

  public void setBuyPrice(String buyPrice) {
    this.buyPrice = buyPrice;
  }

  public String getChangedPrice() {
    return changedPrice;
  }

  public void setChangedPrice(String changedPrice) {
    this.changedPrice = changedPrice;
  }

  public String getSellDate() {
    return sellDate;
  }

  public void setSellDate(String sellDate) {
    this.sellDate = sellDate;
  }

  public String getSellPrice() {
    return sellPrice;
  }

  public void setSellPrice(String sellPrice) {
    this.sellPrice = sellPrice;
  }

  public String getPriceRange() {
    return priceRange;
  }

  public void setPriceRange(String priceRange) {
    this.priceRange = priceRange;
  }

  public String getReturns() {
    return returns;
  }

  public void setReturns(String returns) {
    this.returns = returns;
  }

  public String getTargetMessage() {
    return targetMessage;
  }

  public void setTargetMessage(String targetMessage) {
    this.targetMessage = targetMessage;
  }

  public String getSellNowReturns() {
    return sellNowReturns;
  }

  public void setSellNowReturns(String sellNowReturns) {
    this.sellNowReturns = sellNowReturns;
  }

  public String getRecommendation() {
    return recommendation;
  }

  public void setRecommendation(String recommendation) {
    this.recommendation = recommendation;
  }

  public boolean isTargetReached() {
    return targetReached;
  }

  public void setTargetReached(boolean targetReached) {
    this.targetReached = targetReached;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
