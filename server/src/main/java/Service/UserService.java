package Service;

import HandlerOBJs.RegisterRequest;
import HandlerOBJs.RegisterResult;
import dataaccess.DataAccessException;
import dataaccess.TempStorage.MemoryAuthDAO;
import dataaccess.UserDAO;

public class UserService {
    private final MemoryAuthDAO userDAO;

    public UserService(MemoryAuthDAO userDAO) {
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        /*
        public Pet addPet(Pet pet) throws ResponseException {
            if (pet.type() == PetType.DOG && pet.name().equals("fleas")) {
                throw new ResponseException(ResponseException.Code.ClientError, "Error: no dogs with fleas");
            }
            return dataAccess.addPet(pet);
            */
         return userDAO.createAuth(registerRequest.username());
        }
    }
    //public LoginResult login(LoginRequest loginRequest) {}
    //public void logout(LogoutRequest logoutRequest) {}
}