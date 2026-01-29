package chess.PieceMoveCalc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalc implements PieceMoveCalc {
    private final ChessPiece piece;
    private final ChessPosition position;
    private final ChessBoard board;

    private final int startingPosition;
    private final int promotionLocation;
    private final int progressOnBoard;

    public PawnMoveCalc(ChessPiece piece, ChessPosition position, ChessBoard board) {
        this.piece = piece;
        this.position = position;
        this.board = board;
        this.startingPosition = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? 2 : 7;
        this.promotionLocation = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? 7 : 2;
        this.progressOnBoard = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? 1 : -1;
    }

    public boolean isFrontBlocked() {
        ChessPiece boardPiece = board.getPiece(new ChessPosition(position.getRow()+progressOnBoard, position.getColumn()));
        return boardPiece != null;
    }

    public boolean isMovePossible(ChessPosition direction) {
        ChessPiece boardPiece;
        if(board.isOutOfBounds(direction)) {
            return false;
        }
        boardPiece = board.getPiece(direction);
        if(direction.getColumn() == position.getColumn()) {
            return boardPiece == null && !isFrontBlocked();
        } else {
            if(boardPiece != null) {
                return !boardPiece.getTeamColor().equals(piece.getTeamColor());
            }
        }
        return false;
    }

    public Collection<ChessPosition> getDirections() {
        Collection<ChessPosition> directions = new ArrayList<>();
        if(startingPosition == position.getRow()) {
            directions.add(new ChessPosition(position.getRow() + (progressOnBoard * 2), position.getColumn()));
        }
        directions.add(new ChessPosition(position.getRow() + progressOnBoard, position.getColumn()));
        directions.add(new ChessPosition(position.getRow() + progressOnBoard, position.getColumn()-1));
        directions.add(new ChessPosition(position.getRow() + progressOnBoard, position.getColumn()+1));

        return directions;
    }

    @Override
    public Collection<ChessMove> getPieceMoves() {
        Collection<ChessPosition> directions = getDirections();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessPiece.PieceType[] promotionPieces = ChessPiece.getPromotions();

        if(position.getRow() == promotionLocation) {
            for(ChessPosition direction : directions) {
                if(isMovePossible(direction)) {
                    for(ChessPiece.PieceType type : promotionPieces) {
                        possibleMoves.add(new ChessMove(position,direction,type));
                    }
                }
            }
        } else {
            for(ChessPosition direction : directions) {
                if(isMovePossible(direction)) {
                    possibleMoves.add(new ChessMove(position,direction,null));
                }
            }
        }
        return possibleMoves;
    }
}
