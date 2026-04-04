package httpobjs;

import chess.ChessGame;

public record UpdateGameResult (String message, ChessGame game){
}
