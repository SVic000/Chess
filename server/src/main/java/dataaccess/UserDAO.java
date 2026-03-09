package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.Collection;

public interface UserDAO {
    void clear() throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    Collection<UserData> getUserStorage() throws DataAccessException;

    UserData getUser(String userName) throws DataAccessException;
}
