package io.arsh.ui;

import io.arsh.Panel;
import io.arsh.game.Board;
import io.arsh.game.Rules;
import io.arsh.game.models.Move;
import io.arsh.game.models.Piece;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class Controls extends MouseAdapter {

    private final Panel panel;
    private final Board board;

    private boolean isGameOver = false;
    private boolean isWhiteTurn = true;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<Move> legalMoves = null;

    public Controls(Panel panel) {
        this.panel = panel;
        this.board = panel.getBoard();
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (isGameOver) {
            resetGame();
            return;
        }

        int col = (event.getX() - panel.getMargin()) / panel.getTileSize();
        int row = (event.getY() - panel.getMargin()) / panel.getTileSize();

        if (isInvalidCoordinates(row, col)) {
            return;
        }

        handleSelection(row, col);
    }

    private void resetGame() {
        panel.setTitle(null);
        board.reset();
        isWhiteTurn = true;
        isGameOver = false;
        clearSelections();
        panel.repaint();
    }

    private void handleSelection(int row, int col) {
        if (isPieceSelected()) {
            if (isLegalMove(row, col)) {
                performMove(row, col);
            } else {
                clearSelections();
                selectPiece(row, col);
            }
        } else {
            selectPiece(row, col);
        }

        panel.repaint();
    }

    private void selectPiece(int row, int col) {
        Piece piece = board.getPiece(row, col);
        if (piece == null || !isCurrentPlayerPiece(piece)) {
            return;
        }

        selectedRow = row;
        selectedCol = col;
        panel.setTile(row, col, Texture.TILE_SELECT);

        legalMoves = Rules.getLegalMoves(board, row, col);
        highlightLegalMoves();
    }

    private void performMove(int toRow, int toCol) {
        board.movePiece(selectedRow, selectedCol, toRow, toCol);
        isWhiteTurn = !isWhiteTurn;
        clearSelections();
        checkGameState();
    }

    private void checkGameState() {
        if (areOnlyKingsLeft()) {
            endGameWithTie();
            return;
        }

        if (isStalemate()) {
            endGameWithTie();
            return;
        }

        if (isCheckmate()) {
            endGameWithWinner();
        } else {
            highlightKingIfInCheck();
        }
    }

    private boolean areOnlyKingsLeft() {
        int pieceCount = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null) {
                    if (!piece.name().endsWith("KING")) {
                        return false;
                    }
                    pieceCount++;
                }
            }
        }
        return pieceCount == 2;
    }

    private void highlightKingIfInCheck() {
        updateKingHighlight(true);
        updateKingHighlight(false);
    }

    private void updateKingHighlight(boolean isWhiteKing) {
        int[] kingPosition = findKing(isWhiteKing);
        if (kingPosition == null) {
            return;
        }

        int kingRow = kingPosition[0];
        int kingCol = kingPosition[1];

        if (Rules.isInCheck(board, isWhiteKing)) {
            if (selectedRow != kingRow || selectedCol != kingCol) {
                panel.setTile(kingRow, kingCol, Texture.TILE_OPTION);
            }
        } else {
            if (selectedRow != kingRow || selectedCol != kingCol) {
                panel.resetTile(kingRow, kingCol);
            }
        }
    }

    private void clearSelections() {
        if (isPieceSelected()) {
            panel.resetTile(selectedRow, selectedCol);
        }
        if (legalMoves != null) {
            for (Move move : legalMoves) {
                panel.resetTile(move.toRow, move.toCol);
            }
        }
        selectedRow = -1;
        selectedCol = -1;
        legalMoves = null;
    }


    private void highlightLegalMoves() {
        for (Move move : legalMoves) {
            Texture tileTexture = (board.getPiece(move.toRow, move.toCol) != null)
                    ? Texture.TILE_OPTION
                    : Texture.TILE_PATH;
            panel.setTile(move.toRow, move.toCol, tileTexture);
        }
    }

    private int[] findKing(boolean isWhiteKing) {
        String kingName = isWhiteKing ? "WHITE_KING" : "BLACK_KING";
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.name().equals(kingName)) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    private boolean isInvalidCoordinates(int row, int col) {
        return col < 0 || col >= 8 || row < 0 || row >= 8;
    }

    private boolean isPieceSelected() {
        return selectedRow != -1 && selectedCol != -1;
    }

    private boolean isLegalMove(int row, int col) {
        if (legalMoves == null) {
            return false;
        }
        for (Move move : legalMoves) {
            if (move.toRow == row && move.toCol == col) {
                return true;
            }
        }
        return false;
    }

    private boolean isCurrentPlayerPiece(Piece piece) {
        return (isWhiteTurn && Rules.isWhite(piece)) || (!isWhiteTurn && !Rules.isWhite(piece));
    }

    private boolean isStalemate() {
        return !hasLegalMoves(isWhiteTurn) && !Rules.isInCheck(board, isWhiteTurn);
    }

    private boolean isCheckmate() {
        return !hasLegalMoves(isWhiteTurn) && Rules.isInCheck(board, isWhiteTurn);
    }

    private boolean hasLegalMoves(boolean isWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && (Rules.isWhite(piece) == isWhite)) {
                    if (!Rules.getLegalMoves(board, r, c).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void endGameWithTie() {
        panel.setTitle(Texture.TITLE_TIE);
        isGameOver = true;
    }

    private void endGameWithWinner() {
        panel.setTitle(isWhiteTurn ? Texture.TITLE_BLACK_WINS : Texture.TITLE_WHITE_WINS);
        isGameOver = true;
    }

}