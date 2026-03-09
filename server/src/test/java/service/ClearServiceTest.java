package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.memoryStorage.MemoryAuthDAO;
import dataaccess.memoryStorage.MemoryGameDAO;
import dataaccess.memoryStorage.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    static final UserDAO USER_STORAGE = new MemoryUserDAO();
    static final GameDAO GAME_STORAGE = new MemoryGameDAO();
    static final AuthDAO AUTH_STORAGE = new MemoryAuthDAO();

    static final ClearService SERVICE = new ClearService(USER_STORAGE, AUTH_STORAGE, GAME_STORAGE);

    @Test
    void clear() throws DataAccessException {
        USER_STORAGE.createUser(new UserData("username","password","email"));
        AUTH_STORAGE.createAuth("username");
        GAME_STORAGE.createGame("gameName");

        SERVICE.clear();

        assertEquals(0, AUTH_STORAGE.getAuthStorage().size());
        assertEquals(0, USER_STORAGE.getUserStorage().size());
        assertEquals(0, GAME_STORAGE.getGameStorage().size());
    }

}
