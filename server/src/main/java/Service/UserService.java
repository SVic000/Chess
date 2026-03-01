package Service;

import HandlerOBJs.LoginRequest;
import HandlerOBJs.LoginResult;
import HandlerOBJs.RegisterRequest;
import HandlerOBJs.RegisterResult;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
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
            throw new BadRequestResponse("Error: bad request");
        }
        UserData user = new UserData(registerRequest.username(),registerRequest.password(), registerRequest.email());
        try {
            userDAO.createUser(user);
            AuthData authData = authDAO.createAuth(user.username());
            result = new RegisterResult(user.username(), authData.token(), "");
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        return result;
    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        LoginResult result;
        if(loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestResponse("Error: bad request");
        }
        try {
            UserData user = userDAO.getUser(loginRequest.username());
            if(user.password().equals(loginRequest.password())) {
                AuthData authData = authDAO.createAuth(user.username());
                result = new LoginResult(user.username(),authData.token(),"");
            } else {
                throw new UnauthorizedResponse("Error: Incorrect password");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        return result;
    }
    //public void logout(LogoutRequest logoutRequest) {}
}