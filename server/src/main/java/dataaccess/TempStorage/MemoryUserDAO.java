package dataaccess.TempStorage;

import dataaccess.DataAccessException;
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
    public void updateUser(UserData user) throws DataAccessException{
        if(userStorage.containsKey(user.username())) {
            userStorage.put(user.username(),user);
            return;
        }
        throw new DataAccessException("User not found");
    }

    @Override
    public void createUser(UserData user) throws DataAccessException{
        if(!userStorage.containsKey(user.username())) {
            userStorage.put(user.username(),user);
            return;
        }
        throw new DataAccessException("User Already Exists");
    }

    @Override
    public void deleteUser(UserData user) throws DataAccessException{
       if(userStorage.containsKey(user.username())) {
           if(userStorage.get(user.username()).equals(user)) {
               userStorage.remove(user.username());
               return;
           }
           throw new DataAccessException("User info doesn't match given username");
       }
       throw new DataAccessException("User was not found");
    }

    @Override
    public UserData getUser(String userName) throws DataAccessException{
        if(userStorage.containsKey(userName)) {
            return userStorage.get(userName);
        }
        throw new DataAccessException("User wasn't found");
    }
}
