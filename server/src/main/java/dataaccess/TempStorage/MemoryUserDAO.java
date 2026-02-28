package dataaccess.TempStorage;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpResponseException;
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
        throw new BadRequestResponse("Error: bad request");
    }

    @Override
    public void createUser(UserData user) throws DataAccessException{
        if(!userStorage.containsKey(user.username())) {
            userStorage.put(user.username(),user);
            return;
        }
        throw new ForbiddenResponse("Error: already taken");
    }

    @Override
    public void deleteUser(UserData user) throws DataAccessException{
       if(userStorage.containsKey(user.username())) {
           if(userStorage.get(user.username()).equals(user)) {
               userStorage.remove(user.username());
               return;
           }
           throw new BadRequestResponse("Error: bad request");
       }
       throw new BadRequestResponse("Error: bad request");
    }

    @Override
    public UserData getUser(String userName) throws DataAccessException{
        if(userStorage.containsKey(userName)) {
            return userStorage.get(userName);
        }
        throw new BadRequestResponse("Error: bad request");
    }
}
