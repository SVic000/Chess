package dataaccess.storage;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> userStorage = new HashMap<>();

    @Override
    public void clear() {
        userStorage.clear();
    }

    @Override
    public Collection<UserData> getUserStorage() {
        return userStorage.values();
    }


    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (!userStorage.containsKey(user.username())) {
            userStorage.put(user.username(), user);
            return;
        }
        throw new ForbiddenResponse("Error: already taken");
    }

    @Override
    public UserData getUser(String userName) throws DataAccessException {
        if (userStorage.containsKey(userName)) {
            return userStorage.get(userName);
        }
        throw new UnauthorizedResponse("Error: User not found");
    }
}
