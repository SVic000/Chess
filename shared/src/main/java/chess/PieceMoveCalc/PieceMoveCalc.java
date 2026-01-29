package chess.PieceMoveCalc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;


public interface PieceMoveCalc {

    Collection<ChessMove> getPieceMoves();

    default Collection<ChessMove> slide(ChessPiece piece, ChessPosition position, int[] direction, ChessBoard board) {
        int row = position.getRow();
        int col = position.getColumn();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessPosition wherePosition;
        ChessPiece boardPiece;

        while (true) {
            wherePosition = new ChessPosition(row + direction[0], col + direction[1]);
            if (board.isOutOfBounds(wherePosition)) {
                break;
            }
            boardPiece = board.getPiece(wherePosition);
            if (boardPiece == null) {
                possibleMoves.add(new ChessMove(position, wherePosition, null));
                row += direction[0];
                col += direction[1];
            } else {
                if (!boardPiece.getTeamColor().equals(piece.getTeamColor())) {
                    possibleMoves.add(new ChessMove(position, wherePosition, null));
                }
                break;
            }
        }
        return possibleMoves;
    }

    default Collection<ChessMove> simpleMoveCalc(ChessPiece piece, ChessPosition position, ChessBoard board,
                                                 int[][] directions) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessPosition wherePosition;
        ChessPiece boardPiece;

        for (int[] direction : directions) {
            wherePosition = new ChessPosition(position.getRow() + direction[0], position.getColumn() + direction[1]);
            if (!board.isOutOfBounds(wherePosition)) {
                boardPiece = board.getPiece(wherePosition);
                if (boardPiece == null || !boardPiece.getTeamColor().equals(piece.getTeamColor())) {
                    possibleMoves.add(new ChessMove(position, wherePosition, null));
                }
            }
        }
        return possibleMoves;
    }
}
