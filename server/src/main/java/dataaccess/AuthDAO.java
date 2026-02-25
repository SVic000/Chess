package dataaccess;

import HandlerOBJs.RegisterResult;
import model.AuthData;

public interface AuthDAO {
    void clear();
    RegisterResult createAuth(String username) throws DataAccessException;
    AuthData getAuth(String token);
    void deleteAuth(AuthData authData) throws DataAccessException;
}
