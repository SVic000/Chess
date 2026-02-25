package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;

public interface AuthDAO {
    void clear();
    void createAuth(String username) throws DataAccessException;
    AuthData getAuth(String token);
    void deleteAuth(AuthData authData) throws DataAccessException;
}
