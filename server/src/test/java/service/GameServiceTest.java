package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.dbStorage.MySqlAuthDataAccess;
import dataaccess.dbStorage.MySqlGameDataAccess;
import dataaccess.memoryStorage.MemoryAuthDAO;
import dataaccess.memoryStorage.MemoryGameDAO;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.handlers.objects.*;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    static final GameDAO GAME_STORAGE;
    static final AuthDAO AUTH_STORAGE;
    static {
        try {
            GAME_STORAGE = new MySqlGameDataAccess();
            AUTH_STORAGE = new MySqlAuthDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static final GameService SERVICE = new GameService(AUTH_STORAGE,GAME_STORAGE);

    @BeforeEach
    void clear() throws DataAccessException {
        GAME_STORAGE.clear();
        AUTH_STORAGE.clear();
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        CreateGameRequest test = new CreateGameRequest("Name");
        AuthData auth = AUTH_STORAGE.createAuth("username");
        CreateGameResult actual = SERVICE.createGame(test,auth.token());

        assertNotNull(actual);
        assertEquals(1, GAME_STORAGE.listGames().size());
    }

    @Test
    void createNoGivenGameName() throws DataAccessException {
        AuthData auth = AUTH_STORAGE.createAuth("username");
        assertThrows(BadRequestResponse.class, ()->SERVICE.createGame(new CreateGameRequest(""), auth.token()));
    }

    @Test
    void joinGameSuccess() throws DataAccessException {
        GameData game = GAME_STORAGE.createGame("test");
        AuthData auth = AUTH_STORAGE.createAuth("username");

        JoinGameRequest test = new JoinGameRequest("WHITE", game.gameID());
        GameData expectedGame = new GameData(game.gameID(),"username",null,"test",game.game());

        JoinGameResult actualRes = SERVICE.joinGame(test, auth.token());
        GameData actualGame = GAME_STORAGE.getGame(game.gameID());

        assertEquals(1, GAME_STORAGE.listGames().size());
        assertEquals(expectedGame,actualGame);
        assertNotNull(actualRes);
    }

    @Test
    void joinGameColorTaken() throws DataAccessException {
        GameData game = GAME_STORAGE.createGame("test");
        AuthData takenAuth = AUTH_STORAGE.createAuth("taken");
        AuthData auth = AUTH_STORAGE.createAuth("username");

        JoinGameRequest test = new JoinGameRequest("WHITE", game.gameID());
        SERVICE.joinGame(test, takenAuth.token());

        assertThrows(ForbiddenResponse.class,()->SERVICE.joinGame(test,auth.token()));
    }

    @Test
    void listGameSuccess() throws DataAccessException {
        GameData game = GAME_STORAGE.createGame("test");
        GameData game1 = GAME_STORAGE.createGame("test");
        GameData game2 = GAME_STORAGE.createGame("test");
        AuthData takenAuth = AUTH_STORAGE.createAuth("taken");

        Collection<GameData> expected = new ArrayList<>();
        expected.add(game);
        expected.add(game1);
        expected.add(game2);

        ListGameResult actual = SERVICE.listGames(takenAuth.token());
        assertEquals(3, actual.games().size());
        assertArrayEquals(expected.toArray(), actual.games().toArray());
    }

    @Test
    void listGameUnauthorized() {
        assertThrows(UnauthorizedResponse.class, ()->SERVICE.listGames("token"));
    }
}
