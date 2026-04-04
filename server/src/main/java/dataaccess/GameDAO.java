package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    GameData updateGame(int gameID, ChessGame game) throws DataAccessException;

    void joinGame(int gameID, String username, String color) throws DataAccessException;

    void clear() throws DataAccessException;
}
