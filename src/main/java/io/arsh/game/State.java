package io.arsh.game;

import io.arsh.Config;
import io.arsh.ui.Texture;

public class State {
    private final Texture[][] board;

    public State() {
        board = new Texture[Config.BOARD_SIZE][Config.BOARD_SIZE];

        board[0][0] = Texture.BLACK_ROOK;
        board[0][1] = Texture.BLACK_KNIGHT;
        board[0][2] = Texture.BLACK_BISHOP;
        board[0][3] = Texture.BLACK_QUEEN;
        board[0][4] = Texture.BLACK_KING;
        board[0][5] = Texture.BLACK_BISHOP;
        board[0][6] = Texture.BLACK_KNIGHT;
        board[0][7] = Texture.BLACK_ROOK;

        for (int i = 0; i < 8; i++) {
            board[1][i] = Texture.BLACK_PAWN;
        }

        board[7][0] = Texture.WHITE_ROOK;
        board[7][1] = Texture.WHITE_KNIGHT;
        board[7][2] = Texture.WHITE_BISHOP;
        board[7][3] = Texture.WHITE_QUEEN;
        board[7][4] = Texture.WHITE_KING;
        board[7][5] = Texture.WHITE_BISHOP;
        board[7][6] = Texture.WHITE_KNIGHT;
        board[7][7] = Texture.WHITE_ROOK;

        for (int i = 0; i < 8; i++) {
            board[6][i] = Texture.WHITE_PAWN;
        }
    }

    public State(State other) {
        board = new Texture[Config.BOARD_SIZE][Config.BOARD_SIZE];
        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            System.arraycopy(other.board[r], 0, board[r], 0, Config.BOARD_SIZE);
        }
    }

    public Texture getPiece(int row, int col) {
        return board[row][col];
    }

    public void setPiece(int row, int col, Texture piece) {
        board[row][col] = piece;
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = null;
    }

}
