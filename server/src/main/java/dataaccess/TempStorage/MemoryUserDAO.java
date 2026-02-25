package dataaccess.TempStorage;

import dataaccess.UserDAO;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    Map<String, UserData> userStorage = new HashMap<>();
    @Override
    public void clear() {
        userStorage = new HashMap<>();
    }

    @Override
    public void updateUser(UserData user) {

    }

    @Override
    public void createUser(UserData user) {

    }

    @Override
    public void deleteUser(String userName) {

    }

    @Override
    public UserData getUser(String userName) {
        return null;
    }
}
