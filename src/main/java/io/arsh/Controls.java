package io.arsh;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class Controls extends MouseAdapter {

    private final Board board;
    private final State state;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private boolean whiteTurn = true;

    public Controls(Board board, State state) {
        this.board = board;
        this.state = state;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int col = (e.getX() - Config.MARGIN) / Config.TILE_SIZE;
        int row = (e.getY() - Config.MARGIN) / Config.TILE_SIZE;

        if (col < 0 || col >= Config.BOARD_SIZE || row < 0 || row >= Config.BOARD_SIZE) {
            return;
        }

        if (selectedRow == -1 && state.getPiece(row, col) != null) {
            Texture piece = state.getPiece(row, col);

            boolean isWhitePiece = piece.name().startsWith("WHITE");
            if ((whiteTurn && isWhitePiece) || (!whiteTurn && !isWhitePiece)) {
                selectedRow = row;
                selectedCol = col;

                List<int[]> moves = Rules.getLegalMoves(state, row, col);
                board.setHighlightedMoves(moves);

                board.setSelectedTile(row, col);
            }

        } else if (selectedRow != -1) {
            List<int[]> moves = Rules.getLegalMoves(state, selectedRow, selectedCol);

            for (int[] move : moves) {
                if (move[0] == row && move[1] == col) {
                    Texture targetPiece = state.getPiece(row, col);
                    Texture movingPiece = state.getPiece(selectedRow, selectedCol);

                    if (targetPiece != null) {
                        boolean movingIsWhite = movingPiece.name().startsWith("WHITE");
                        boolean targetIsWhite = targetPiece.name().startsWith("WHITE");
                        if (movingIsWhite == targetIsWhite) {
                            continue;
                        }
                    }
                    state.movePiece(selectedRow, selectedCol, row, col);
                    whiteTurn = !whiteTurn;
                    break;
                }
            }

            selectedRow = -1;
            selectedCol = -1;
            board.clearSelectedTile();
            board.clearHighlightedMoves();
        }
        checkGameOver();
        board.repaint();
    }

    private void checkGameOver() {
        boolean whiteInCheck = Rules.isInCheck(state, true);
        boolean blackInCheck = Rules.isInCheck(state, false);

        boolean whiteHasMoves = hasAnyLegalMoves(true);
        boolean blackHasMoves = hasAnyLegalMoves(false);

        if (!whiteHasMoves) {
            if (whiteInCheck) {
                board.setGameResult(Texture.TITLE_BLACK_WINS);
            } else {
                board.setGameResult(Texture.TITLE_TIE);
            }
        } else if (!blackHasMoves) {
            if (blackInCheck) {
                board.setGameResult(Texture.TITLE_WHITE_WINS);
            } else {
                board.setGameResult(Texture.TITLE_TIE);
            }
        }

        int pieceCount = 0;
        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                Texture p = state.getPiece(r, c);
                if (p != null) pieceCount++;
            }
        }

        if (pieceCount == 2) {
            board.setGameResult(Texture.TITLE_TIE);
        }
    }

    private boolean hasAnyLegalMoves(boolean white) {
        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                Texture piece = state.getPiece(r, c);
                if (piece != null && piece.name().startsWith(white ? "WHITE" : "BLACK")) {
                    if (!Rules.getLegalMoves(state, r, c).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
