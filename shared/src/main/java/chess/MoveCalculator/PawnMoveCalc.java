package chess.MoveCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveCalc implements PieceMovesCalc {
    private final ChessPiece piece;
    private final ChessBoard board;
    private final ChessPosition position;
    private final Collection<ChessMove> possibleMoves = new ArrayList<>();
    private final int promotionLocation;
    private final int progress;
    private final int startingLocation;

    // method that sets promotion location white = 7, black = 2

    public PawnMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
        this.promotionLocation = ChessGame.TeamColor.WHITE.equals(piece.getTeamColor()) ? 7 : 2;
        this.startingLocation = ChessGame.TeamColor.WHITE.equals(piece.getTeamColor()) ? 2 : 7;
        progress = piece.getTeamColor().equals(ChessGame.TeamColor.WHITE) ? 1 : -1;
    }

    public boolean checkFront() {
        ChessPiece boardPiece = board.getPiece(new ChessPosition(position.getRow()+progress, position.getColumn()));
        return boardPiece == null;
    }

    /** checking possible moves true if you can move there, false if not */
    private boolean checkCorner(ChessPosition checkPosition) {
        if(board.outOfBounds(checkPosition)) {
            return false;
        }
        ChessPiece boardPiece = board.getPiece(checkPosition);
        if(checkFront() && checkPosition.getColumn() == position.getColumn()) { // checking if you can even move forward
            return boardPiece == null;
        }
        try {
            return switch (boardPiece.getTeamColor()) {
                case WHITE -> piece.getTeamColor() != ChessGame.TeamColor.WHITE && checkPosition.getColumn() != position.getColumn();
                case BLACK -> piece.getTeamColor() != ChessGame.TeamColor.BLACK && checkPosition.getColumn() != position.getColumn();
            };
        } catch(Exception e) {
            return false;
        }
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
            for (ChessPosition corner : directions) {
                if (checkCorner(corner)) {
                    for(ChessPiece.PieceType type : promotablePieces) {
                        whereMove = new ChessMove(position, corner, type);
                        possibleMoves.add(whereMove);
                    }
                }
            }
        } else {
            for (ChessPosition corner : directions) {
                if (checkCorner(corner)) {
                    whereMove = new ChessMove(position, corner, null);
                    possibleMoves.add(whereMove);
                }
            }
        }

        return possibleMoves;
    }
}
