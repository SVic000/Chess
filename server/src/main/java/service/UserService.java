package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.UserData;
import server.handlers.objects.*;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        RegisterResult result;
        if (registerRequest.username() == null || registerRequest.email() == null || registerRequest.password() == null) {
            throw new BadRequestResponse("Error: bad request");
        }
        UserData user = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        try {
            userDAO.createUser(user);
            AuthData authData = authDAO.createAuth(user.username());
            result = new RegisterResult(user.username(), authData.token(), "");
        } catch (DataAccessException e) {
            throw new ForbiddenResponse("Error: already taken");
        }
        return result;
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        LoginResult result;
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestResponse("Error: bad request");
        }
        try {
            UserData user = userDAO.getUser(loginRequest.username());
            if (userDAO.verifyUserPassword(loginRequest.username(), loginRequest.password())) {
                AuthData authData = authDAO.createAuth(user.username());
                result = new LoginResult(user.username(), authData.token(), "");
            } else {
                throw new UnauthorizedResponse("Error: Incorrect password");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        return result;
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        validateAuthorization(logoutRequest.authToken());

        AuthData authData = authDAO.getAuth(logoutRequest.authToken());
        if (authData == null) {
            throw new UnauthorizedResponse("Error: Unauthorized");
        }
        authDAO.deleteAuth(authData);
    }

    private void validateAuthorization(String auth) throws DataAccessException {
        if (auth != null) {
            AuthData authData = authDAO.getAuth(auth);
            if (authData == null) {
                throw new UnauthorizedResponse("Error: Unauthorized");
            }
        } else {
            throw new UnauthorizedResponse("Error: Unauthorized");
        }
    }
}