package httpobjs;
import chess.ChessGame;
import chess.ChessMove;

public record UpdateGameRequest (int gameID, ChessGame.TeamColor color, ChessMove move, String authToken) {
}
