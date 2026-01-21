package chess.MoveCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveCalc implements PieceMovesCalc {
    private final ChessPiece piece;
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessGame.TeamColor color;
    private final Collection<ChessMove> possibleMoves = new ArrayList<>();
    private final int promotionLocation;
    private final int startingLocation;

    // method that sets promotion location white = 7, black = 2

    public PawnMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
        this.color = piece.getTeamColor();
        this.promotionLocation = ChessGame.TeamColor.WHITE.equals(color) ? 2 : 7;
        this.startingLocation =
    }

    private boolean checkFront(ChessBoard board, int[] corner) {
        ChessPiece boardFrontPiece;
        if(ChessGame.TeamColor.WHITE.equals(color)) {
            boardFrontPiece = board.getPiece(new ChessPosition(position.getRow() + 1, position.getColumn()));
        } else {
            boardFrontPiece = board.getPiece(new ChessPosition(position.getRow() - 1, position.getColumn()));
        }
        return boardFrontPiece != null;
    }

    /** checking possible moves true if you can move there, false if not */
    private boolean checkCorner(ChessBoard board, int[] corner) {
        if(corner[1] < 1 || corner[1] > 8) {
            return false; // out of the board, skip this one
        }
        if(checkFront(board,corner) && corner[1] == position.getColumn()) { // checking if you can even move forward
            return false;
        }
        ChessPiece boardPiece = board.getPiece(new ChessPosition(corner[0], corner[1]));
        try {
            return switch (boardPiece.getTeamColor()) {
                case WHITE -> color != ChessGame.TeamColor.WHITE && corner[1] != position.getColumn();
                case BLACK -> color != ChessGame.TeamColor.BLACK && corner[1] != position.getColumn();
            };
        } catch(Exception e) {
            return corner[1] == position.getColumn();
        }
    }

    @Override
    public Collection<ChessMove> getPieceMoves() {
        List<ChessPiece.PieceType> promotion = piece.promotionsList();
        ChessMove whereMove;

        if (color == ChessGame.TeamColor.WHITE) { // go up
            if (position.getRow() == 2) { // haven't moved
                int[][] corners = {{4, position.getColumn()}, {3, position.getColumn()}, {3, position.getColumn() - 1}, {3, position.getColumn() + 1}};
                for (int[] ints : corners) {
                    if (checkCorner(board, ints)) {
                        whereMove = new ChessMove(position, new ChessPosition(ints[0], ints[1]), null);
                        possibleMoves.add(whereMove);
                    }
                }
            } else if (position.getRow() == promotionLocation) { // ready to promote // any moves, add promote to it
                int[][] corners = {{8,position.getColumn()},{8,position.getColumn()+1},{8,position.getColumn()-1}};
                for (int[] ints : corners) {
                    if(checkCorner(board,ints)) {
                        for(ChessPiece.PieceType type : promotion) {
                            whereMove = new ChessMove(position, new ChessPosition(ints[0],ints[1]), type);
                            possibleMoves.add(whereMove);
                        }
                    }
                }
            }  else { // somewhere in the middle
                    int[][] corners = {{position.getRow()+1, position.getColumn()}, {position.getRow()+1, position.getColumn()+1}, {position.getRow()+1, position.getColumn()-1}};
                    for (int[] ints : corners) {
                        if (checkCorner(board, ints)) {
                            whereMove = new ChessMove(position, new ChessPosition(ints[0], ints[1]), null);
                            possibleMoves.add(whereMove);
                        }
                    }
            }
        }
         else { // black moves
            if (position.getRow() == 7) { // haven't moved
                int[][] corners = {{5, position.getColumn()}, {6, position.getColumn()}, {6, position.getColumn() - 1}, {6, position.getColumn() + 1}};
                for (int[] ints : corners) {
                    if (checkCorner(board, ints)) {
                        whereMove = new ChessMove(position, new ChessPosition(ints[0], ints[1]), null);
                        possibleMoves.add(whereMove);
                    }
                }
            } else if (position.getRow() == promotionLocation) { // ready to promote // any moves, add promote to it
                int[][] corners = {{1,position.getColumn()},{1,position.getColumn()+1},{1,position.getColumn()-1}};
                for (int[] ints : corners) {
                    if(checkCorner(board,ints)) {
                        for(ChessPiece.PieceType type : promotion) {
                            whereMove = new ChessMove(position, new ChessPosition(ints[0],ints[1]), type);
                            possibleMoves.add(whereMove);
                        }
                    }
                }
            }  else { // somewhere in the middle
                int[][] corners = {{position.getRow()-1, position.getColumn()}, {position.getRow()-1, position.getColumn()+1}, {position.getRow()-1, position.getColumn()-1}};
                for (int[] ints : corners) {
                    if (checkCorner(board, ints)) {
                        whereMove = new ChessMove(position, new ChessPosition(ints[0], ints[1]), null);
                        possibleMoves.add(whereMove);
                    }
                }
            }
        }
        return possibleMoves;
    }
}
