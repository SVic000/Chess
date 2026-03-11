package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.dbStorage.MySqlAuthDataAccess;
import dataaccess.dbStorage.MySqlUserDataAccess;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.handlers.objects.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static final UserDAO USER_STORAGE;
    static final AuthDAO AUTH_STORAGE;
    static final UserService SERVICE;

    static {
        try {
            USER_STORAGE = new MySqlUserDataAccess();
            AUTH_STORAGE = new MySqlAuthDataAccess();
            SERVICE = new UserService(USER_STORAGE, AUTH_STORAGE);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clear() throws DataAccessException {
        USER_STORAGE.clear();
        AUTH_STORAGE.clear();
    }

    @Test
    void registerUserSuccess() throws DataAccessException {
        RegisterRequest test = new RegisterRequest("username", "password", "email");
        RegisterResult actual = SERVICE.register(test);

        assertEquals(test.username(), actual.username());
        assertNotEquals(null, actual.authToken());
        assertEquals(1, AUTH_STORAGE.getAuthStorage().size());
    }

    @Test
    void registerUserNoUsername() throws DataAccessException {
        RegisterRequest test = new RegisterRequest(null, "password", "email");
        assertThrows(BadRequestResponse.class, () -> SERVICE.register(test));
    }

    @Test
    void loginSuccess() throws DataAccessException {
        LoginRequest test = new LoginRequest("username", "password");
        USER_STORAGE.createUser(new UserData("username", "password", "email"));

        LoginResult actual = SERVICE.login(test);
        Collection<AuthData> authStorage = AUTH_STORAGE.getAuthStorage();
        assertNotNull(AUTH_STORAGE.getAuth(actual.authToken()));
    }

    @Test
    void loginNotExistingUser() {
        LoginRequest test = new LoginRequest("username", "password");
        assertThrows(UnauthorizedResponse.class, () -> SERVICE.login(test));
    }

    @Test
    void logoutSuccess() throws DataAccessException {
        AuthData auth = AUTH_STORAGE.createAuth("username");
        LogoutRequest test = new LogoutRequest(auth.token());
        SERVICE.logout(test);

        assertEquals(0, AUTH_STORAGE.getAuthStorage().size());
    }

    @Test
    void logoutUnauthorized() {
        LogoutRequest test = new LogoutRequest("");
        assertThrows(UnauthorizedResponse.class, () -> SERVICE.logout(test));
    }

}
