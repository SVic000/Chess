package chess.PieceMoveCalc;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalc implements PieceMoveCalc {
    private final ChessPiece piece;
    private final ChessPosition position;
    private final ChessBoard board;

    public KingMoveCalc(ChessPiece piece, ChessPosition position, ChessBoard board) {
        this.piece = piece;
        this.position = position;
        this.board = board;
    }

    @Override
    public Collection<ChessMove> getPieceMoves() {
        int[][] directions = {{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1}};
        ChessPosition wherePosition;
        ChessPiece boardPiece;
        Collection<ChessMove> possibleMoves = new ArrayList<>();

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
