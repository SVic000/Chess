package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalc implements PieceMovesCalc {
    private final ChessPiece piece;
    private final ChessBoard board;
    private final ChessPosition position;
    private final Collection<ChessMove> possibleMovement = new ArrayList<>();
    private final int [][] direction =  {{1,-1},{1,1},{-1,1},{-1,-1}};

    public BishopMoveCalc (ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> getPieceMoves(){
        Collection<ChessMove> move;
        for(int i = 0; i< 4; i++) {
            move = slide(position, piece, direction[i], board);
            possibleMovement.addAll(move);
        }
        return possibleMovement;
    }
}
