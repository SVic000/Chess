package dataaccess;

import chess.ChessGame;
import dataaccess.dbstorage.MySqlGameDataAccess;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {
    static final GameDAO STORAGE;

    static {
        try {
            STORAGE = new MySqlGameDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clear() throws DataAccessException {
        STORAGE.clear();
    }

    @Test
    void testClear() throws DataAccessException {
        assertDoesNotThrow(STORAGE::clear);
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        // create game and make sure the chess board is the same as a new chess board
        assertDoesNotThrow(() -> STORAGE.createGame("name"));
        assertEquals(1, STORAGE.listGames().size());
    }

    @Test
    void createGameWithNullName() throws DataAccessException {
        assertThrows(ForbiddenResponse.class, () -> STORAGE.createGame(null));
    }

    @Test
    void getGameSuccess() throws DataAccessException {
        GameData test = STORAGE.createGame("name");

        assertDoesNotThrow(() -> STORAGE.getGame(test.gameID()));
        GameData game = STORAGE.getGame(test.gameID());
        ChessGame newGame = new ChessGame();
        assertEquals(newGame, game.game());
    }

    @Test
    void getGameWithNoID() {
        assertThrows(BadRequestResponse.class, () -> STORAGE.getGame(0));
    }

    @Test
    void listGamesSuccess() {

    }

    @Test
    void joinGameSuccess() throws DataAccessException {
        GameData test = STORAGE.createGame("game");

        assertDoesNotThrow(() -> STORAGE.joinGame(test.gameID(), "username", "WHITE"));
        GameData retrieved = STORAGE.getGame(test.gameID());
        assertEquals("username", retrieved.whiteUsername());
    }

    @Test
    void joinGameNoGameID() throws DataAccessException {
        GameData test = STORAGE.createGame("game");

        assertThrows(BadRequestResponse.class, () -> STORAGE.joinGame(0, "username", "WHITE"));
    }
}
