package Service;

import HandlerOBJs.CreateGameRequest;
import HandlerOBJs.CreateGameResult;
import HandlerOBJs.JoinGameRequest;
import HandlerOBJs.JoinGameResult;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.GameData;

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
        if (gameData.whiteUsername().equals(request.playerColor())) {
            if (gameData.whiteUsername().isEmpty()) {
                gameDAO.joinGame(request.gameID(), authData.username(), request.playerColor());
                return new JoinGameResult("");
            }
        } else {
            if (gameData.blackUsername().isEmpty()) {
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


    /*

    public ListGameResponse listGames(ListGameRequest request) {
        // Verification handled in server
        // compile the entire list of games into a response
        // return the response with empty message
        // (only error is an unauthorized error)
    }

    */
}
