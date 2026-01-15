package chess.MoveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;

class BishopMove implements PieceMoves {
    private final ChessPiece piece;
    private final ChessBoard board;
    private final ChessPosition position;
    private Collection<ChessMove> possibleMovement;

    public BishopMove(ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> getPieceMoves(){
        int [][] direction =  {{1,-1},{1,1},{-1,1},{-1,-1}};
        // look into using streams for this!
        Collection<ChessMove> move1 = this.slide(this.position, this.piece, direction[0], this.board);
        Collection<ChessMove> move2 = this.slide(this.position, this.piece, direction[1], this.board);
        Collection<ChessMove> move3 = this.slide(this.position, this.piece, direction[2], this.board);
        Collection<ChessMove> move4 = this.slide(this.position, this.piece, direction[3], this.board);


        return this.possibleMovement;
    }

}
