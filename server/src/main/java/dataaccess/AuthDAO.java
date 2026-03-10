package dataaccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDAO {
    void clear() throws DataAccessException;

    Collection<AuthData> getAuthStorage() throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(String token) throws DataAccessException;

    void deleteAuth(AuthData authData) throws DataAccessException;
}
