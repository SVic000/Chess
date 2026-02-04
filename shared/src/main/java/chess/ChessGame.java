package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    private TeamColor currentTurn = TeamColor.WHITE;

    public ChessGame() {
        board.resetBoard();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurn);
    }

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
       // will use "isincheck/isincheckmate/isinstalemate
        ChessPiece boardPiece = board.getPiece(startPosition);
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessBoard originalBoard = (ChessBoard) board.clone();
        boolean canMove = false;
        TeamColor originalColor = currentTurn;
        if(boardPiece != null) {
            for(ChessMove move : boardPiece.pieceMoves(board, startPosition)) {
                board = (ChessBoard) originalBoard.clone();
                currentTurn = originalColor;
                testMove(move);
                if (!isInCheck(boardPiece.getTeamColor())) {
                    canMove = true;
                    possibleMoves.add(move);
                    }
                }
            }
        board = originalBoard;
        currentTurn = originalColor;
        return canMove ? possibleMoves : new ArrayList<>();
    }

    public void testMove(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        ChessPiece boardPiece = board.getPiece(startPosition);
        if (promotionPiece != null) {
                board.addPiece(move.getEndPosition(), new ChessPiece(boardPiece.getTeamColor(), promotionPiece));
        } else {
            board.addPiece(endPosition, boardPiece);
        }
       board.removePiece(startPosition);
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        ChessPiece boardPiece = board.getPiece(move.getStartPosition());
        if (validMoves.contains(move) && boardPiece != null && boardPiece.getTeamColor().equals(currentTurn)) {
            testMove(move);
            currentTurn = currentTurn.equals(TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public Collection<ChessPosition> findAnyTeamLocations(TeamColor teamColor, boolean friendly) {
        ChessPiece boardPiece;
        Collection<ChessPosition> pieceLocations = new ArrayList<>();

        for(int row = 0; row < board.grid.length; row++) {
            for(int col = 0; col < board.grid.length; col++) {
                boardPiece = board.grid[row][col];
                if(boardPiece != null && !boardPiece.getTeamColor().equals(teamColor) && !friendly) {
                    pieceLocations.add(new ChessPosition(row+1,col+1));
                } else if (boardPiece != null && boardPiece.getTeamColor().equals(teamColor) && friendly) {
                    pieceLocations.add(new ChessPosition(row+1,col+1));
                }
            }
        }
        return pieceLocations;
    }

    public ChessPosition findKing(TeamColor teamColor) {
        ChessPiece boardPiece;
        ChessPosition kingPosition = null;
        for(int row = 0; row < board.grid.length; row++) {
            for(int col = 0; col < board.grid.length; col++) {
                boardPiece = board.getPiece(new ChessPosition(row+1,col+1));
                if(boardPiece != null && boardPiece.getTeamColor().equals(teamColor) && boardPiece.getPieceType().equals(ChessPiece.PieceType.KING)) {
                    kingPosition = new ChessPosition(row+1,col+1);
                }
            }
        }
        return kingPosition;
    }


    public boolean isInCheck(TeamColor teamColor) {
        boolean isInCheck = false;
        Collection<ChessPosition> enemyLocations = findAnyTeamLocations(teamColor, false);
        ChessPiece boardPiece;
        ChessPosition kingPosition = findKing(teamColor);

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
        if (isInCheck(teamColor)) {
            Collection<ChessPosition> teamLocation = findAnyTeamLocations(teamColor, true);
            ChessBoard originalBoard = (ChessBoard) board.clone();
            boolean isInCheckMate = true;
            TeamColor originalColor = currentTurn;

            for(ChessPosition teamPos : teamLocation) {
                for(ChessMove move : validMoves(teamPos)) {
                    try {
                        makeMove(move);
                        if(!isInCheck(teamColor)){
                            isInCheckMate = false;
                            break; }
                        else {
                            board = (ChessBoard) originalBoard.clone();
                            currentTurn = originalColor;
                        }
                    } catch (InvalidMoveException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            board = originalBoard;
            currentTurn = originalColor;
            return isInCheckMate;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // not in check
        // if every move puts you in check return true! else return false
        if(!isInCheck(teamColor)) {
            Collection<ChessPosition> teamLocations = findAnyTeamLocations(teamColor,true);
            ChessBoard originalBoard = (ChessBoard) board.clone();
            boolean isInStaleMate = false;
            chess.ChessGame.TeamColor originalColor = currentTurn;

            for(ChessPosition teamPos : teamLocations)  {
                for(ChessMove move : validMoves(teamPos)) {
                    try {
                        makeMove(move);
                        if(!isInCheck(teamColor)) {
                            isInStaleMate = false;
                            break;
                        }
                        board = (ChessBoard) originalBoard.clone();
                        currentTurn = originalColor;
                        isInStaleMate = true;
                    } catch (InvalidMoveException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            board = originalBoard;
            currentTurn = originalColor;
            return isInStaleMate;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
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
