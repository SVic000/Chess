package chess.MoveCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveCalc implements PieceMovesCalc{
    private final ChessPiece piece;
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessGame.TeamColor color;
    private final Collection<ChessMove> possibleMoves = new ArrayList<>();

    public PawnMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
        this.color = piece.getTeamColor();
    }
    // directions this one can move
    // [0,1] or [0,2] if position = 1 for White or position = 7 for Black (not for board)

    @Override
    public Collection<ChessMove> getPieceMoves() {
        if(color == ChessGame.TeamColor.WHITE) { // go up
            // if where you're going is 7, promote
            // check directions [0,1], if != null for [1,1],[-1,1]
            // if position.row = 1, add [0,2] as possible move
        } else { // go down
            // if where you're going is 0, promote
            // check directions [0,-1], if != null for [1,-1],[-1,-1] add
            // if position.row = 6, add [0,-2] as possible move
        }
    }
}
