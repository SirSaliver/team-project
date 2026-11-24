package usecase.addtowatchlist;

import entities.Watchlist;
import java.io.IOException;

/**
 * The watchlist DAO interface for the Add To Watchlist Use Case.
 */
public interface AddToWatchlistWatchlistDataAccessInterface {
  /**
   * Loads the saved watchlist.
   *
   * @return The saved watchlist, or an empty watchlist if none has been saved yet
   * @throws IOException If the watchlist cannot be read
   */
  Watchlist readWatchlist() throws IOException;

  /**
   * Saves the watchlist, replacing whatever was saved before.
   *
   * @param watchlist The watchlist to save
   * @throws IOException If the watchlist cannot be written
   */
  void saveWatchlist(Watchlist watchlist) throws IOException;
}
