package chess.MoveCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveCalc implements PieceMovesCalc {
    private final ChessPiece piece;
    private final ChessBoard board;
    private final ChessPosition position;
    private final int promotionLocation;
    private final int progress;
    private final int startingLocation;

    public PawnMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
        this.promotionLocation = ChessGame.TeamColor.WHITE.equals(piece.getTeamColor()) ? 7 : 2;
        this.startingLocation = ChessGame.TeamColor.WHITE.equals(piece.getTeamColor()) ? 2 : 7;
        progress = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? 1 : -1;
    }

    public boolean isFrontBlocked() {
        ChessPiece boardPiece = board.getPiece(new ChessPosition(position.getRow()+progress, position.getColumn()));
        return boardPiece == null;
    }

    /** checking possible moves true if you can move there, false if not */
    private boolean isMovePossible(ChessPosition checkPosition) {
        if (board.outOfBounds(checkPosition)) {
            return false;
        }
        ChessPiece boardPiece = board.getPiece(checkPosition);
        if (isFrontBlocked() && checkPosition.getColumn() == position.getColumn()) { // checking if you can even move forward
            return boardPiece == null;
        }
        if(boardPiece == null) return false;
        return switch (boardPiece.getTeamColor()) {
            case WHITE ->
                    piece.getTeamColor() != ChessGame.TeamColor.WHITE && checkPosition.getColumn() != position.getColumn();
            case BLACK ->
                    piece.getTeamColor() != ChessGame.TeamColor.BLACK && checkPosition.getColumn() != position.getColumn();
        };
    }

    public Collection<ChessPosition> getDirections() {
        Collection<ChessPosition> directions = new ArrayList<>();
        if(position.getRow() == startingLocation) { // add the initial jump
            directions.add(new ChessPosition(position.getRow() + (2 * progress), position.getColumn()));
        }
        directions.add(new ChessPosition(position.getRow() + progress, position.getColumn()));
        directions.add(new ChessPosition(position.getRow() + progress, position.getColumn() + 1));
        directions.add(new ChessPosition(position.getRow() + progress, position.getColumn() - 1));
        return directions;
    }


    @Override
    public Collection<ChessMove> getPieceMoves() {
        ChessMove whereMove;
        List<ChessPiece.PieceType> promotablePieces = ChessPiece.promotionsList();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        Collection<ChessPosition> directions = getDirections();

        if(position.getRow() == promotionLocation) {
            for (ChessPosition direction : directions) {
                if (isMovePossible(direction)) {
                    for(ChessPiece.PieceType type : promotablePieces) {
                        whereMove = new ChessMove(position, direction, type);
                        possibleMoves.add(whereMove);
                    }
                }
            }
        } else {
            for (ChessPosition direction : directions) {
                if (isMovePossible(direction)) {
                    whereMove = new ChessMove(position, direction, null);
                    possibleMoves.add(whereMove);
                }
            }
        }
        return possibleMoves;
    }
}
