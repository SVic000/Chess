package chess.MoveCalculator;
import chess.*;

import java.util.ArrayList;
import java.util.Collection;


public interface PieceMovesCalc {

Collection<ChessMove> getPieceMoves();

default Collection<ChessMove> slide(ChessPosition myPosition, ChessPiece thisPiece, int[] direction, ChessBoard board) {
    boolean canSlide = true;
    int row = myPosition.getRow();
    int col = myPosition.getColumn();
    Collection<ChessMove> possibleMoves = new ArrayList<>();

    while(canSlide) {
        canSlide = false;
        ChessPosition whereMove = new ChessPosition(row + direction[0], col + direction[1]);
        if(whereMove.getRow()-1 > 7 || whereMove.getRow() -1 < 0 || whereMove.getColumn() -1  > 7 || whereMove.getColumn()-1  < 0) { // out of grid
            break;
        }
        ChessPiece boardPiece = board.getPiece(whereMove);
        if(boardPiece == null) { // it's empty! add it and keep going
            possibleMoves.add(new ChessMove(myPosition, whereMove, null));
            row += direction[0];
            col += direction[1]; // keep chugging forward
            canSlide = true;
        } else if(boardPiece.getTeamColor() == thisPiece.getTeamColor()) { // same team, stop iterating
            break;
        } else { // not on team, add to moves but then break!
            possibleMoves.add(new ChessMove(myPosition, whereMove, null));
            break;
        }
    }
    return possibleMoves;
} }
