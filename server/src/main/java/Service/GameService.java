package Service;

import HandlerOBJs.CreateGameRequest;
import HandlerOBJs.CreateGameResult;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.http.BadRequestResponse;
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

    /*
    public JoinGameResponse joinGame(JoinGameRequest request) {
        // given auth and color
         // Auth Verification handled in server
        // check to see if requested color is taken (WHITE/BLACK)
        // let player join if previous checks work
        // send back response with empty message
    }

    public ListGameResponse listGames(ListGameRequest request) {
        // only given auth
        // Verification handled in server
        // compile the entire list of games into a response
        // return the response with empty message
        // (only error is an unauthorized error)
    }

    */
}
