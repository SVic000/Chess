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

    public BishopMoveCalc (ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> getPieceMoves(){
        int [][] direction =  {{1,-1},{1,1},{-1,1},{-1,-1}};
        Collection<ChessMove> move;
        for(int i = 0; i< 4; i++) {
            move = this.slide(this.position, this.piece, direction[i], this.board);
            this.possibleMovement.addAll(move);
        }
        return this.possibleMovement;
    }
}
