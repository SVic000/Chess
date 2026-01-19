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
            return true; // out of the board, skip this one
        }
        ChessPiece boardPiece = board.getPiece(new ChessPosition(corner[0], corner[1]));
        return switch (boardPiece.getTeamColor()) {
            case null ->
                // checking forward movement, if column == your current column, you can move there if not you can't
                    position.getColumn() == corner[1];
            case WHITE -> color != ChessGame.TeamColor.WHITE;
            case BLACK -> color != ChessGame.TeamColor.BLACK;
        };
    }

    @Override
    public Collection<ChessMove> getPieceMoves() {
        int whereTooCol = position.getColumn();
        ChessPiece.PieceType[] promotable = piece.promotions();
        ChessMove whereMove;
        int idol = 0;

        if (color == ChessGame.TeamColor.WHITE) { // go up
            // if where you're going is 8, promote (context of pos)
            // check directions [0,1], if != null for [1,1],[-1,1]
            // if position.row = 2, add [0,2] as possible move
            if (position.getRow() == 2) { // haven't moved
                int[][] corners = {{4,position.getColumn()},{3,position.getColumn()},{3,position.getColumn()-1},{3,position.getColumn()+1}};
                for(int[] ints : corners ) {
                    if(checkCorner(board, ints)) {
                        whereMove = new ChessMove(position, new ChessPosition(ints[0],ints[1]), null);
                        possibleMoves.add(whereMove);
                    }
                }
            } else if (position.getRow() == 7) { // ready to promote // any moves, add promote to it
                idol +=1;
            } else { // somewhere in the middle
                idol +=1;
            }
        } else {
            // go down
            // if where you're going is 0, promote
            // check directions [0,-1], if != null for [1,-1],[-1,-1] add
            // if position.row = 6, add [0,-2] as possible move
            idol +=1;
        }
        return possibleMoves;
    }
}
