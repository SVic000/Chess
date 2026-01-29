package chess.PieceMoveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalc implements pieceMoveCalc {
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
        return simpleMoveCalc(piece,position,board,directions);
    }
}
