package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.storage.MemoryAuthDAO;
import dataaccess.storage.MemoryUserDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static final UserDAO USER_STORAGE = new MemoryUserDAO();
    static final AuthDAO AUTH_STORAGE = new MemoryAuthDAO();

    static final UserService SERVICE = new UserService(USER_STORAGE,AUTH_STORAGE);

    @BeforeEach
    void clear() {
        USER_STORAGE.clear();
        AUTH_STORAGE.clear();
    }

    // register pos run one, register again but perhaps change password?
    @Test
    void registerUserSuccess() {}

    @Test
    void registerUserFailure() {}

    // login normal, login different password
    @Test
    void loginSuccess(){}

    @Test
    void loginNotExistingUser(){}


    // logout normal, maybe no negative test since auth done in Server?
    @Test
    void logoutSuccess() {}
}
