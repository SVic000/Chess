package service;

import chess.ChessGame;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import httpobjs.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import model.AuthData;
import model.GameData;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        new ValidateAuthorization(authDAO, authToken);

        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new BadRequestResponse("Error: bad request");
        }
        GameData game = gameDAO.createGame(request.gameName());
        return new CreateGameResult(game.gameID(), "");
    }

    public JoinGameResult joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        new ValidateAuthorization(authDAO, authToken);

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
        new ValidateAuthorization(authDAO, auth);
        return new ListGameResult(gameDAO.listGames().stream().toList(), "");
    }

}
