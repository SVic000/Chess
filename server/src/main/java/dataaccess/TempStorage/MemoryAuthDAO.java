package dataaccess.TempStorage;

import HandlerOBJs.RegisterResult;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    Map<String, AuthData> authStorage = new HashMap<>();

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void clear() {
        authStorage = new HashMap<>();
    }

    @Override
    public RegisterResult createAuth(String username) throws DataAccessException{
        for(AuthData auth : authStorage.values()) {
            if(auth.username().equals(username)) {
                throw new DataAccessException("User already has a token tied to their username");
            }
        }
        String token = generateToken();
        AuthData value = new AuthData(token, username);
        authStorage.put(token,value);
        return null;
    }

    @Override
    public AuthData getAuth(String token) {
        return authStorage.getOrDefault(token, null);
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException {
        if(authStorage.containsKey(authData.token())) {
            if(authStorage.get(authData.token()).equals(authData)) {
                authStorage.remove(authData.token());
                return;
            }
            throw new DataAccessException("Token Tied to user Isn't the same");
        }
        throw new DataAccessException("Token not found in Memory");
    }
}
