package Service;

import HandlerOBJs.RegisterRequest;
import HandlerOBJs.RegisterResult;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import io.javalin.http.HttpResponseException;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        RegisterResult result;
        if(registerRequest.username() == null || registerRequest.email() == null || registerRequest.password() == null) {
            throw new DataAccessException("Error: bad request", 400);
        }
        UserData user = new UserData(registerRequest.username(),registerRequest.password(), registerRequest.email());
        try {
            userDAO.createUser(user);
            AuthData authData = authDAO.createAuth(user.username());
            result = new RegisterResult(user.username(), authData.token(), "");
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage(),e.statusCode);
        }
        return result;
    }
    //public LoginResult login(LoginRequest loginRequest) {}
    //public void logout(LogoutRequest logoutRequest) {}
}