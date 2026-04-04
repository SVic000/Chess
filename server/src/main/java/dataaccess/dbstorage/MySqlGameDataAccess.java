package dataaccess.dbstorage;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import model.GameData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDataAccess implements GameDAO {
    static int gameID = 1;
    private final ConfigureAndExecute configureAndExecute = new ConfigureAndExecute();

    public MySqlGameDataAccess() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` INT NOT NULL,
              `gameName` varchar(256) NOT NULL,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `chessGame` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            )
            """
        };
        configureAndExecute.configureDatabase(createStatements);
    }

    @Override
    public GameData createGame(String gameName) {
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        String chessGameSerial = new Gson().toJson(gameData.game());

        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername,gameName, chessGame) VALUES (?,?,?,?,?)";
        try {
            configureAndExecute.executeUpdate(statement,
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    chessGameSerial);
        } catch (DataAccessException e) {
            throw new ForbiddenResponse("Error: already taken");
        }
        gameID += 1;
        return gameData;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()));
        }
        throw new BadRequestResponse("Error: Game not found");
    }

    @Override
    public GameData updateGame(int gameID, ChessGame game) throws DataAccessException {
        String chessGameSerial = new Gson().toJson(game);
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE games SET chessGame = ? WHERE gameID = ?";
            configureAndExecute.executeUpdate(statement, chessGameSerial, gameID);
        } catch (SQLException e) {
            throw new DataAccessException("Error: unable to update game");
        }
        return getGame(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> result = new ArrayList();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public void joinGame(int gameID, String username, String color) throws DataAccessException {
        GameData current = getGame(gameID); // will throw error if not in db
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement;
            String chessGameSerial = new Gson().toJson(current.game());
            if (color.equals("WHITE")) {
                statement = "UPDATE games SET whiteUsername = ? WHERE gameID = ?";
                configureAndExecute.executeUpdate(statement, username, gameID);
            } else {
                statement = "UPDATE games SET blackUsername = ? WHERE gameID = ?";
                configureAndExecute.executeUpdate(statement, username, gameID);
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Error: unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE games";
        configureAndExecute.executeUpdate(statement);
        gameID = 1;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var gameName = rs.getString("gameName");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var chess = rs.getString("chessGame");
        ChessGame chessGame = new Gson().fromJson(chess, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
    }

}
