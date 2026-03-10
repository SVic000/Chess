package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.GameData;
import server.handlers.objects.*;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        validateAuthorization(authToken);

        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new BadRequestResponse("Error: bad request");
        }
        GameData game = gameDAO.createGame(request.gameName());
        return new CreateGameResult(game.gameID(), "");
    }


    public JoinGameResult joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        validateAuthorization(authToken);

        AuthData authData = authDAO.getAuth(authToken);
        GameData gameData = gameDAO.getGame(request.gameID());
        if (!isRequestedColorValid(request)) {
            throw new BadRequestResponse("Error: bad request");
        }
        if (request.playerColor().equals("WHITE")) {
            if (gameData.whiteUsername() == null) {
                gameDAO.joinGame(request.gameID(), authData.username(), request.playerColor());
                return new JoinGameResult("");
            }
        } else {
            if (gameData.blackUsername() == null) {
                gameDAO.joinGame(request.gameID(), authData.username(), request.playerColor());
                return new JoinGameResult("");
            }
        }
        throw new ForbiddenResponse("Error: already taken");
    }

    private boolean isRequestedColorValid(JoinGameRequest request) {
        if (request.playerColor() == null || request.playerColor().isEmpty()) {
            return false;
        }
        return !(!request.playerColor().equals("WHITE") & !request.playerColor().equals("BLACK"));
    }


    public ListGameResult listGames(String auth) throws DataAccessException {
        validateAuthorization(auth);
        return new ListGameResult(gameDAO.listGames().stream().toList());
    }

    private void validateAuthorization(String auth) {
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
