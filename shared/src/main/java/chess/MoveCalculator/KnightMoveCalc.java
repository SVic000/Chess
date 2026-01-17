package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMoveCalc implements PieceMovesCalc{
    private final ChessPiece piece;
    private final ChessBoard board;
    private final ChessPosition position;
    private final int[][] direction = {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{2,-1},{-2,1},{-2,-1}};
    private Collection<ChessMove> possibleMovment = new ArrayList<>();

    public KnightMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
    }

    @Override
    public Collection<ChessMove> getPieceMoves() {
        ChessMove whereMove;
        for(int[] ints : direction) {
            whereMove = new ChessMove(position, new ChessPosition(position.getRow() + ints[0], position.getColumn() + ints[1]), null);
            if(whereMove.getEndPosition().getRow()-1 < 0 || whereMove.getEndPosition().getRow()-1 > 7 || whereMove.getEndPosition().getColumn()-1 < 0 || whereMove.getEndPosition().getColumn()-1 >7 ) {
                continue; // out of bounds, keep going
            }
            ChessPiece boardPiece = board.getPiece(whereMove.getEndPosition());
            if(boardPiece == null || boardPiece.getTeamColor() != piece.getTeamColor()){
                // null or enemy, add to list
                possibleMovment.add(whereMove);
            }
        }
        return possibleMovment;
    }
}
