package dataaccess;

import model.UserData;

public interface UserDAO {
    void clear();
    void updateUser(UserData user);
    void createUser(UserData user);
    void deleteUser(String userName);
    UserData getUser(String userName);
}
