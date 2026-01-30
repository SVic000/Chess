package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board;
    TeamColor currentTurn;

    public ChessGame() {
        this.board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) { currentTurn = team; }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new InvalidMoveException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public Collection<ChessPosition> enemyTeamLocations(TeamColor teamColor) {
        // find all pieces of the same type and return their location on the board
        // with location I can get piece type and go from there
        ChessPiece boardPiece;
        Collection<ChessPosition> enemyLocations = new ArrayList<>();
        for(int row = 0; row < board.grid.length; row++) {
            for(int col = 0; col < board.grid.length; col++) {
                boardPiece = board.grid[row][col];
                if(boardPiece != null && boardPiece.getTeamColor().equals(teamColor)) {
                    enemyLocations.add(new ChessPosition(row,col));
                }
            }
        }
        return enemyLocations;
    }

    public ChessPosition findKing() {
        ChessPiece boardPiece;
        ChessPosition kingPosition = null;
        for(int row = 0; row < board.grid.length; row++) {
            for(int col = 0; col < board.grid.length; col++) {
                boardPiece = board.grid[row][col];
                if(boardPiece != null && boardPiece.getTeamColor().equals(currentTurn) && boardPiece.getPieceType().equals(ChessPiece.PieceType.KING)) {
                    kingPosition = new ChessPosition(row,col);
                }
            }
        }
        return kingPosition; // bad code talk to TA
    }


    public boolean isInCheck(TeamColor teamColor) {
        // check all opponent possible moves, if one can capture king, return true
        boolean isInCheck = false;
        Collection<ChessPosition> enemyLocations = enemyTeamLocations(currentTurn);
        ChessPiece boardPiece;
        ChessPosition kingPosition = findKing();

        for(ChessPosition enemyPos : enemyLocations) {
            boardPiece = board.getPiece(enemyPos);
            for(ChessMove move : boardPiece.pieceMoves(board,enemyPos)) {
                if (move.getEndPosition().equals(kingPosition)) {
                    isInCheck = true;
                    break;
                }
            }
        }
        return isInCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        board.resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
