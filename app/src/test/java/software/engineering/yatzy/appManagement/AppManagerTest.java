package software.engineering.yatzy.appManagement;

import org.junit.Assert;
import org.junit.Test;

import software.engineering.yatzy.game.Game;
import software.engineering.yatzy.game.GameState;
import software.engineering.yatzy.game.PlayerParticipation;

public class AppManagerTest {

    private LoggedInUser loggedInUser = new LoggedInUser("Anton", "fake a session key", 0, 0);

    // UPDATES FROM SERVER
    private String newGame1 = "15:1:TestGame1:PENDING:Abbe:PENDING:next:Ali:PENDING:next:Anton:PENDING:next:Ludde:PENDING:next:Sebbe:HOST:null";
    private String newGame2 = "15:2:TestGame2:PENDING:Abbe:PENDING:next:Ali:PENDING:next:Anton:PENDING:next:Ludde:PENDING:next:Sebbe:HOST:null";
    private String updateParticipation = "34:1:ACCEPTED";

    @Test
    public void newGameFromServer() {
        // Initial number of games
        int initialSize = AppManager.getInstance().gameList.size();
        // Update from server
        AppManager.getInstance().update(newGame1);
        // Updated number of games
        int newSize = AppManager.getInstance().gameList.size();

        Assert.assertNotEquals("Server adds new game", initialSize, newSize);
    }

    @Test
    public void playerParticipationUpdate() {
        AppManager.getInstance().update(newGame1);
        AppManager.getInstance().loggedInUser = loggedInUser;

        PlayerParticipation initialParticipation;
        PlayerParticipation updatedParticipation;

        // Player's participation before update from server
        initialParticipation = AppManager.getInstance().getGameByGameID(1).getPlayerByName("Anton").participation;

        // Update from server
        AppManager.getInstance().update(updateParticipation);

        // Player's participation after update from server
        updatedParticipation = AppManager.getInstance().getGameByGameID(1).getPlayerByName("Anton").participation;

        Assert.assertNotEquals("Player participation update", initialParticipation, updatedParticipation);
    }

    @Test
    // Testing the pass-by-reference qualities of getGameByID()
    public void testGetGameByID() {
        AppManager.getInstance().update(newGame1);
        AppManager.getInstance().loggedInUser = loggedInUser;

        Game game1 = AppManager.getInstance().getGameByGameID(1);
        game1.setGameName("New Game Name");
        game1.state = GameState.ONGOING;

        Game game2 = AppManager.getInstance().getGameByGameID(1);

        // Verify that game1 and game2 points to the same location in memory
        Assert.assertEquals("Pass-by-reference: name", game1.getGameName(), game2.getGameName());
        Assert.assertEquals("Pass-by-reference: state", game1.state, game2.state);
    }

    @Test
    // Testing the pass-by-reference qualities of getGameByID()
    public void testInitialParticipation() {
        AppManager.getInstance().update(newGame2);

        AppManager.getInstance().loggedInUser = loggedInUser;

        PlayerParticipation expectedParticipation = PlayerParticipation.PENDING;
        PlayerParticipation actualParticipation = AppManager.getInstance().getGameByGameID(2).getPlayerByName("Anton").participation;

        Assert.assertEquals("Player participation", expectedParticipation, actualParticipation);

    }

}