package chess.PieceMoveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public class KingMoveCalc implements PieceMoveCalc {
    private final ChessPiece piece;
    private final ChessPosition position;
    private final ChessBoard board;

    public KingMoveCalc(ChessPiece piece, ChessPosition position, ChessBoard board) {
        this.piece = piece;
        this.position = position;
        this.board = board;
    }

    @Override
    public Collection<ChessMove> getPieceMoves() {
        int[][] directions = {{1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};
        return simpleMoveCalc(piece, position, board, directions);
    }
}
