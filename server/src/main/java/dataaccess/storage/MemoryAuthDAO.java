package dataaccess.storage;

import dataaccess.AuthDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
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
    public AuthData createAuth(String username) {
        String token = generateToken();
        AuthData value = new AuthData(token, username);
        authStorage.put(token, value);
        return value;
    }

    @Override
    public AuthData getAuth(String token) {
        return authStorage.getOrDefault(token, null);
    }

    @Override
    public void deleteAuth(AuthData authData) {
        if (authStorage.containsKey(authData.token())) {
            if (authStorage.get(authData.token()).equals(authData)) {
                authStorage.remove(authData.token());
                return;
            }
            throw new BadRequestResponse("Error: token tied to user doesn't match");
        }
        throw new UnauthorizedResponse("Error: unauthorized");
    }
}
