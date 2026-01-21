package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalc implements PieceMovesCalc {
    private final ChessPiece piece;
    private final ChessPosition position;
    private final ChessBoard board;
    private final int [][] direction = {{1,0},{-1,0},{0,1},{0,-1}};

    public RookMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> getPieceMoves() {
        Collection<ChessMove> possibleMovement = new ArrayList<>();
        for (int[] ints : direction) {
            possibleMovement.addAll(slide(position, piece, ints, board));
        }
        return possibleMovement;
    }
}
