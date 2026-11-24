package usecase.setriskprofile;

import usecase.shared.projection.SellProjection;

/**
 * Output data for the Set Risk Profile use case.
 */
public class SetRiskProfileOutputData {
  private final String ticker;
  private final SellProjection projection;

  /**
   * Constructs a new SetRiskProfileOutputData.
   *
   * @param projection Calculated selling projection
   */
  public SetRiskProfileOutputData(String ticker, SellProjection projection) {
    this.ticker = ticker;
    this.projection = projection;
  }

  public String getTicker() {
    return ticker;
  }

  public SellProjection getProjection() {
    return this.projection;
  }
}
