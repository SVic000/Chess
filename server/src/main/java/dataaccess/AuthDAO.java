package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void clear();

    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(String token);

    void deleteAuth(AuthData authData) throws DataAccessException;
}
