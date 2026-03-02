package dataaccess;

import model.GameData;
import model.UserData;

import java.util.Collection;

public interface UserDAO {
    void clear();

    void createUser(UserData user) throws DataAccessException;

    Collection<UserData> getUserStorage();

    UserData getUser(String userName) throws DataAccessException;
}
