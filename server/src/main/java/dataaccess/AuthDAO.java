package dataaccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDAO {
    void clear();

    Collection<AuthData> getAuthStorage();

    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(String token);

    void deleteAuth(AuthData authData) throws DataAccessException;
}
