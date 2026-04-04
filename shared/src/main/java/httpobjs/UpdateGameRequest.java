package httpobjs;
import chess.ChessMove;

public record UpdateGameRequest (int gameID, String color, ChessMove move, String authToken) {
}
