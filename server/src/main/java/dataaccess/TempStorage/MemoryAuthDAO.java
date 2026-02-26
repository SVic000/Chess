package dataaccess.TempStorage;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import io.javalin.http.HttpResponseException;
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
    public AuthData createAuth(String username) throws DataAccessException{
        for(AuthData auth : authStorage.values()) {
            if(auth.username().equals(username)) {
                throw new DataAccessException("Error: bad request", 400);
            }
        }
        String token = generateToken();
        AuthData value = new AuthData(token, username);
        authStorage.put(token,value);
        return value;
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
            throw new DataAccessException("Error: token tied to user doesn't match", 400);
        }
        throw new DataAccessException("Error: unauthorized", 401);
    }
}
