package chess.PieceMoveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalc implements PieceMoveCalc {
    ChessPiece piece;
    ChessPosition position;
    ChessBoard board;

    public KnightMoveCalc(ChessPiece piece, ChessPosition position, ChessBoard board) {
        this.piece = piece;
        this.position = position;
        this.board = board;
    }

    @Override
    public Collection<ChessMove> getPieceMoves() {
        int[][] directions = {{2,1},{2,-1},{1,2},{-1,2},{1,-2},{-1,-2},{-2,1},{-2,-1}};
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessPosition wherePosition;
        ChessPiece boardPiece;

        for(int[] direction : directions) {
            wherePosition = new ChessPosition(position.getRow() + direction[0], position.getColumn() + direction[1]);
            if(!board.isOutOfBounds(wherePosition)) {
                boardPiece = board.getPiece(wherePosition);
                if(boardPiece == null || !boardPiece.getTeamColor().equals(piece.getTeamColor())) {
                    possibleMoves.add(new ChessMove(position,wherePosition,null));
                }
            }
        }
        return possibleMoves;
    }
}
