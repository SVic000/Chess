package dataaccess;

import dataaccess.dbStorage.MySqlAuthDataAccess;
import dataaccess.dbStorage.MySqlUserDataAccess;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {
    static final AuthDAO STORAGE;

    static {
        try {
            STORAGE = new MySqlAuthDataAccess();
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
    void getAuthStorageSuccess() throws DataAccessException {
        AuthData test = STORAGE.createAuth("username");
        AuthData test1 = STORAGE.createAuth("username1");

        assertDoesNotThrow(STORAGE::getAuthStorage);
        assertEquals(2, STORAGE.getAuthStorage().size());
        Collection<AuthData> list = STORAGE.getAuthStorage();

        assertTrue(list.contains(test));
        assertTrue(list.contains(test1));
    }

    @Test
    void createAuthSuccess() {
        // I can't really test a negative since this doesn't throw an error
        // it just passes an error from a private method
        assertDoesNotThrow(() -> STORAGE.createAuth("username"));
    }

    @Test
    void getAuthSuccess() throws DataAccessException {
        AuthData test = STORAGE.createAuth("username");

        assertDoesNotThrow(() -> STORAGE.getAuth(test.token()));
        AuthData retrieved = STORAGE.getAuth(test.token());

        assertEquals("username", retrieved.username());
    }

    @Test
    void getAuthNoToken() {
        assertThrows(UnauthorizedResponse.class, () -> STORAGE.getAuth("token"));
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        // negative test checked in getAuth
        AuthData test = STORAGE.createAuth("username");

        assertDoesNotThrow(() -> STORAGE.deleteAuth(test));
        assertEquals(0, STORAGE.getAuthStorage().size());
    }
}
