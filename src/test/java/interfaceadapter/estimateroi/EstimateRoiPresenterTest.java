package interfaceadapter.estimateroi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import usecase.estimateroi.EstimateRoiOutputData;

/**
 * Tests for {@link EstimateRoiPresenter} and {@link EstimateRoiViewModel}.
 */
class EstimateRoiPresenterTest {
  private final EstimateRoiViewModel viewModel = new EstimateRoiViewModel();
  private final EstimateRoiPresenter presenter = new EstimateRoiPresenter(viewModel);
  private final EstimateRoiOutputData result =
          new EstimateRoiOutputData(10, 110.0, 1210.0, 110.0, 10.0, 1);

  @Test
  void successShowsResult() {
    presenter.prepareSuccessView(result);

    assertSame(result, viewModel.getState().getResult());
    assertNull(viewModel.getState().getErrorMessage());
  }

  @Test
  void failureKeepsPreviousEstimate() {
    presenter.prepareSuccessView(result);
    presenter.prepareFailView("Not enough historical data available.");

    assertSame(result, viewModel.getState().getResult());
    assertEquals("Not enough historical data available.",
            viewModel.getState().getErrorMessage());
  }

  @Test
  void clearRemovesEstimateAndError() {
    presenter.prepareSuccessView(result);
    presenter.prepareFailView("Some error");
    viewModel.clear();

    assertNull(viewModel.getState().getResult());
    assertNull(viewModel.getState().getErrorMessage());
  }

  @Test
  void clearNotifiesTheView() {
    final String[] firedProperty = new String[1];
    viewModel.addPropertyChangeListener(event -> firedProperty[0] = event.getPropertyName());
    viewModel.clear();

    assertEquals(EstimateRoiViewModel.STATE_PROPERTY, firedProperty[0]);
  }
}
