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
    private int selectedRow = -1, selectedCol = -1;
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
        if (isInvalidCoordinates(row, col)) return;

        if (isPieceSelected() && isLegalMove(row, col)) {
            performMove(row, col);
        } else {
            selectPiece(row, col);
        }

        panel.repaint();
    }

    private void resetGame() {
        panel.setTitle(null);
        board.reset();
        isWhiteTurn = true;
        isGameOver = false;
        clearSelections();
        panel.repaint();
    }

    private void selectPiece(int row, int col) {
        Piece piece = board.getPiece(row, col);
        if (piece == null || piece.isWhite() != isWhiteTurn) return;

        clearSelections();
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
        if (areOnlyKingsLeft() || isStalemate()) {
            endGame(Texture.TITLE_TIE);
        } else if (isCheckmate()) {
            endGame(isWhiteTurn ? Texture.TITLE_BLACK_WINS : Texture.TITLE_WHITE_WINS);
        } else {
            highlightKingIfInCheck();
        }
    }

    private boolean areOnlyKingsLeft() {
        int count = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && !p.isKing()) return false;
                if (p != null) count++;
            }
        }
        return count == 2;
    }

    private void highlightKingIfInCheck() {
        highlightKing(true);
        highlightKing(false);
    }

    private void highlightKing(boolean isWhiteKing) {
        int[] pos = findKing(isWhiteKing);
        if (pos == null) return;

        int row = pos[0], col = pos[1];
        boolean inCheck = Rules.isInCheck(board, isWhiteKing);
        if (selectedRow != row || selectedCol != col) {
            panel.setTile(row, col, inCheck ? Texture.TILE_OPTION : null);
            if (!inCheck) panel.resetTile(row, col);
        }
    }

    private int[] findKing(boolean isWhite) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.isKing() && p.isWhite() == isWhite) return new int[]{r, c};
            }
        return null;
    }

    private boolean isInvalidCoordinates(int row, int col) {
        return row < 0 || row >= 8 || col < 0 || col >= 8;
    }

    private boolean isPieceSelected() {
        return selectedRow != -1 && selectedCol != -1;
    }

    private boolean isLegalMove(int row, int col) {
        return legalMoves != null && legalMoves.stream()
                .anyMatch(move -> move.toRow == row && move.toCol == col);
    }

    private void clearSelections() {
        if (isPieceSelected()) panel.resetTile(selectedRow, selectedCol);
        if (legalMoves != null) legalMoves.forEach(move -> panel.resetTile(move.toRow, move.toCol));
        selectedRow = selectedCol = -1;
        legalMoves = null;
    }

    private void highlightLegalMoves() {
        if (legalMoves == null) return;
        for (Move move : legalMoves) {
            Texture texture = board.getPiece(move.toRow, move.toCol) != null ? Texture.TILE_OPTION : Texture.TILE_PATH;
            panel.setTile(move.toRow, move.toCol, texture);
        }
    }

    private boolean isStalemate() {
        return !hasLegalMoves(isWhiteTurn) && !Rules.isInCheck(board, isWhiteTurn);
    }

    private boolean isCheckmate() {
        return !hasLegalMoves(isWhiteTurn) && Rules.isInCheck(board, isWhiteTurn);
    }

    private boolean hasLegalMoves(boolean white) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p != null && p.isWhite() == white && !Rules.getLegalMoves(board, r, c).isEmpty())
                    return true;
            }
        return false;
    }

    private void endGame(Texture title) {
        panel.setTitle(title);
        isGameOver = true;
    }
}
