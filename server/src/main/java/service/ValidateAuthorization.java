package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;

public class ValidateAuthorization {
    public ValidateAuthorization(AuthDAO authDAO, String auth) throws DataAccessException {
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
