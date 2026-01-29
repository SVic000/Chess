package chess.PieceMoveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalc implements pieceMoveCalc {
    private final ChessPiece piece;
    private final ChessPosition position;
    private final ChessBoard board;

    public BishopMoveCalc(ChessPiece piece, ChessPosition position, ChessBoard board) {
        this.piece = piece;
        this.position = position;
        this.board = board;
    }

    @Override
    public Collection<ChessMove> getPieceMoves() {
        int[][] directions =  {{1,1},{1,-1},{-1,1},{-1,-1}};
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for(int[] direction : directions) {
            possibleMoves.addAll(slide(piece,position,direction,board));
        }
        return possibleMoves;
    }
}
