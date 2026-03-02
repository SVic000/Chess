package Service;

import HandlerOBJs.*;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.List;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request) {
        // Authorization handled in Server
        if(request.gameName() == null) {
            throw new BadRequestResponse("Error: bad request");
        }
        GameData game = gameDAO.createGame(request.gameName());
        return new CreateGameResult(game.gameID(), "");
    }


    public JoinGameResult joinGame(JoinGameRequest request, String auth) {
        // given game ID and color
        // Auth Verification handled in server
        AuthData authData = authDAO.getAuth(auth);
        GameData gameData = gameDAO.getGame(request.gameID());
        if(!isRequestedColorValid(request)) {
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
            if(request.playerColor() == null || request.playerColor().isEmpty()) {
                return false;
            }
        return !(!request.playerColor().equals("WHITE") & !request.playerColor().equals("BLACK"));
    }


    public ListGameResult listGames() {
        return new ListGameResult(gameDAO.listGames().stream().toList());
    }
}
