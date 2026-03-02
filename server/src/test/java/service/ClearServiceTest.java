package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.storage.MemoryAuthDAO;
import dataaccess.storage.MemoryGameDAO;
import dataaccess.storage.MemoryUserDAO;
import model.GameData;
import model.UserData;
import model.AuthData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    static final UserDAO userStorage = new MemoryUserDAO();
    static final GameDAO gameStorage = new MemoryGameDAO();
    static final AuthDAO authStorage = new MemoryAuthDAO();

    static final ClearService service = new ClearService(userStorage,authStorage,gameStorage);

    @Test
    void clear() throws DataAccessException {
        userStorage.createUser(new UserData("username","password","email"));
        authStorage.createAuth("username");
        gameStorage.createGame("gameName");

        service.clear();

        assertEquals(0, authStorage.getAuthStorage().size());
        assertEquals(0, userStorage.getUserStorage().size());
        assertEquals(0, gameStorage.getGameStorage().size());
    }

}
