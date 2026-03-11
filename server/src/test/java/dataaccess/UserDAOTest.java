package dataaccess;
import static org.junit.jupiter.api.Assertions.*;

import dataaccess.dbstorage.MySqlUserDataAccess;
import io.javalin.http.UnauthorizedResponse;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;


public class UserDAOTest {
    static final UserDAO STORAGE;

    static {
        try {
            STORAGE = new MySqlUserDataAccess();
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
    void createUserSuccess() throws DataAccessException {
        UserData test = new UserData("Username","Password","email");

        assertDoesNotThrow(()-> STORAGE.createUser(test));
        assertEquals(1, STORAGE.getUserStorage().size());
    }

    @Test
    void createUserMissingEmail() {
        UserData test = new UserData("user","password", null);
        assertThrows(DataAccessException.class,() -> STORAGE.createUser(test));
    }

    @Test
    void getUserSuccess() throws DataAccessException {
        UserData test = new UserData("Username","Password","email");
        STORAGE.createUser(test);

        assertDoesNotThrow(() -> STORAGE.getUser("Username"));
        assertEquals(1,STORAGE.getUserStorage().size());
    }

    @Test
    void getUserWithNoAuthorizedUser() {
        assertThrows(UnauthorizedResponse.class, () -> STORAGE.getUser("notInDB"));
    }


    @Test
    void verifyUserPasswordSuccess() throws DataAccessException {
        // add user to db and check to see if given string matches
        UserData test = new UserData("Username","Password","email");
        STORAGE.createUser(test);
        assertDoesNotThrow(()->STORAGE.verifyUserPassword("Username","Password"));
        assertTrue(()-> {
            try {
                return STORAGE.verifyUserPassword("Username","Password");
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        });
        }

    @Test
    void VerifyUserWrongPassword() throws DataAccessException {
        UserData test = new UserData("Username","Password","email");
        STORAGE.createUser(test);

        // this only throws the DA exception if getUser throws one, so I don't have a negative test
        // since I don't want to test getUser here

        assertFalse(() -> {
            try {
                return STORAGE.verifyUserPassword("Username", "password");
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void getUserStorageSuccess() throws DataAccessException {
        UserData test = new UserData("Username","Password","email");
        UserData test1 = new UserData("Username1","Password1","email1");
        STORAGE.createUser(test);
        STORAGE.createUser(test1);

        assertDoesNotThrow(STORAGE::getUserStorage);
        assertEquals(2,STORAGE.getUserStorage().size());
        Collection<UserData> listTest = STORAGE.getUserStorage();

       UserData retrieved1 = (UserData) listTest.toArray()[0];
       UserData retrieved2 = (UserData) listTest.toArray()[1];

       assertEquals(retrieved1.username(), test.username());
       assertEquals(retrieved1.email(), test.email());
       assertTrue(STORAGE.verifyUserPassword(test.username(),test.password()));


        assertEquals(retrieved2.username(), test1.username());
        assertEquals(retrieved2.email(), test1.email());
        assertTrue(STORAGE.verifyUserPassword(test1.username(),test1.password()));
    }

}
