package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;

public interface UserDAO {
    void clear();
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String userName) throws DataAccessException;
}
