package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalc implements PieceMovesCalc{
    private final ChessPiece piece;
    private final ChessBoard board;
    private final ChessPosition position;
    private final int[][] direction = {{1,1},{1,0},{1,-1},{0,1},{0,-1},{-1,0},{-1,-1},{-1,1}};
    private final Collection<ChessMove> possibleMovement = new ArrayList<>();

    public KingMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
    }

    @Override
    public Collection<ChessMove> getPieceMoves() {
        ChessMove whereMove;
        for (int[] ints : direction) {
            whereMove = new ChessMove(position, new ChessPosition(position.getRow() + ints[0], position.getColumn() + ints[1]), null);
            if (whereMove.getEndPosition().getRow() - 1 < 0 || whereMove.getEndPosition().getRow() - 1 > 7 || whereMove.getEndPosition().getColumn() - 1 > 7 || whereMove.getEndPosition().getColumn() - 1 < 0) {
                continue; // out of bounds, just skip this one
            }
            ChessPiece boardPiece = board.getPiece(whereMove.getEndPosition());
            if (boardPiece == null || boardPiece.getTeamColor() != piece.getTeamColor()) {
                //either null or enemy, so can capture
                possibleMovement.add(whereMove);
            }
        }
        return possibleMovement;
    }
}
