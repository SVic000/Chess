package chess.MoveCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static chess.ChessPiece.PieceType.*;

public class PawnMoveCalc implements PieceMovesCalc {
    private final ChessPiece piece;
    private final ChessBoard board;
    private final ChessPosition position;
    private final ChessGame.TeamColor color;
    private final Collection<ChessMove> possibleMoves = new ArrayList<>();

    public PawnMoveCalc(ChessPiece piece, ChessBoard board, ChessPosition position) {
        this.piece = piece;
        this.board = board;
        this.position = position;
        this.color = piece.getTeamColor();
    }

    private boolean checkCorner(ChessBoard board, int[] corner) {
        if(corner[1] < 1 || corner[1] > 8) {
            return false; // out of the board, skip this one
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
        int whereTooCol = position.getColumn();
        ChessPiece.PieceType[] promotable = piece.promotions();
        ChessMove whereMove;

        if (color == ChessGame.TeamColor.WHITE) { // go up
            // if where you're going is 8, promote (context of pos)
            // check directions [0,1], if != null for [1,1],[-1,1]
            // if position.row = 2, add [0,2] as possible move
            if (position.getRow() == 2) { // haven't moved
                int[][] corners = {{4, position.getColumn()}, {3, position.getColumn()}, {3, position.getColumn() - 1}, {3, position.getColumn() + 1}};
                for (int[] ints : corners) {
                    if (checkCorner(board, ints)) {
                        whereMove = new ChessMove(position, new ChessPosition(ints[0], ints[1]), null);
                        possibleMoves.add(whereMove);
                    }
                }
            } else if (position.getRow() == 7) { // ready to promote // any moves, add promote to it
                int[][] corners = {{8,position.getColumn()},{8,position.getColumn()+1},{8,position.getColumn()-1}};
                for (int[] ints : corners) {
                    if(checkCorner(board,ints)) {
                        for(ChessPiece.PieceType type : promotable) {
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
         else {
            // go down
            // if where you're going is 0, promote
            // check directions [0,-1], if != null for [1,-1],[-1,-1] add
            // if position.row = 6, add [0,-2] as possible move
            if (position.getRow() == 7) { // haven't moved
                int[][] corners = {{5, position.getColumn()}, {6, position.getColumn()}, {6, position.getColumn() - 1}, {6, position.getColumn() + 1}};
                for (int[] ints : corners) {
                    if (checkCorner(board, ints)) {
                        whereMove = new ChessMove(position, new ChessPosition(ints[0], ints[1]), null);
                        possibleMoves.add(whereMove);
                    }
                }
            } else if (position.getRow() == 2) { // ready to promote // any moves, add promote to it
                int[][] corners = {{1,position.getColumn()},{1,position.getColumn()+1},{1,position.getColumn()-1}};
                for (int[] ints : corners) {
                    if(checkCorner(board,ints)) {
                        for(ChessPiece.PieceType type : promotable) {
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
