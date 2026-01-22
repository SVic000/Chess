package chess.MoveCalculator;
import chess.*;

import java.util.ArrayList;
import java.util.Collection;


public interface PieceMovesCalc {

Collection<ChessMove> getPieceMoves();

default Collection<ChessMove> slide(ChessPosition myPosition, ChessPiece thisPiece, int[] direction, ChessBoard board) {
    int row = myPosition.getRow();
    int col = myPosition.getColumn();
    Collection<ChessMove> possibleMoves = new ArrayList<>();

    while(true) {
        ChessPosition whereMove = new ChessPosition(row + direction[0], col + direction[1]);
        if(board.outOfBounds(whereMove)) { // out of grid
            break;
        }
        ChessPiece boardPiece = board.getPiece(whereMove);
        if(boardPiece == null) {
            possibleMoves.add(new ChessMove(myPosition, whereMove, null));
            row += direction[0];
            col += direction[1];
        } else {
            if (!boardPiece.getTeamColor().equals(thisPiece.getTeamColor())) {
                possibleMoves.add(new ChessMove(myPosition, whereMove, null));
            }
            break;
        }
    }
    return possibleMoves;
} }
