package io.arsh.game;

import io.arsh.game.models.Move;
import io.arsh.game.models.Piece;

public class Board {

    private final Piece[][] pieces;
    private final boolean[][] moved;
    private Move lastMove;

    public Board() {
        pieces = new Piece[8][8];
        moved = new boolean[8][8];

        pieces[0][0] = Piece.BLACK_ROOK;
        pieces[0][1] = Piece.BLACK_KNIGHT;
        pieces[0][2] = Piece.BLACK_BISHOP;
        pieces[0][3] = Piece.BLACK_QUEEN;
        pieces[0][4] = Piece.BLACK_KING;
        pieces[0][5] = Piece.BLACK_BISHOP;
        pieces[0][6] = Piece.BLACK_KNIGHT;
        pieces[0][7] = Piece.BLACK_ROOK;
        for (int i = 0; i < 8; i++) {
            pieces[1][i] = Piece.BLACK_PAWN;
        }

        pieces[7][0] = Piece.WHITE_ROOK;
        pieces[7][1] = Piece.WHITE_KNIGHT;
        pieces[7][2] = Piece.WHITE_BISHOP;
        pieces[7][3] = Piece.WHITE_QUEEN;
        pieces[7][4] = Piece.WHITE_KING;
        pieces[7][5] = Piece.WHITE_BISHOP;
        pieces[7][6] = Piece.WHITE_KNIGHT;
        pieces[7][7] = Piece.WHITE_ROOK;
        for (int i = 0; i < 8; i++) {
            pieces[6][i] = Piece.WHITE_PAWN;
        }

    }

    public Board(Board other) {
        pieces = new Piece[8][8];
        moved = new boolean[8][8];
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                pieces[r][c] = other.pieces[r][c];
                moved[r][c] = other.moved[r][c];
            }
        }
        if (other.lastMove != null) {
            lastMove = new Move(other.lastMove.fromRow, other.lastMove.fromCol,
                    other.lastMove.toRow, other.lastMove.toCol);
        }
    }

    public Piece getPiece(int row, int col) {
        return pieces[row][col];
    }

    public boolean hasMoved(int row, int col) {
        return moved[row][col];
    }

    public Move getLastMove() {
        return lastMove;
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        Piece moving = pieces[fromRow][fromCol];
        if (moving == null) return;

        if (moving.name().contains("KING") && Math.abs(toCol - fromCol) == 2) {
            if (toCol == 6) {
                pieces[toRow][5] = pieces[toRow][7];
                pieces[toRow][7] = null;
                moved[toRow][5] = true;
            } else if (toCol == 2) {
                pieces[toRow][3] = pieces[toRow][0];
                pieces[toRow][0] = null;
                moved[toRow][3] = true;
            }
        }

        if (moving.name().contains("PAWN") && fromCol != toCol && pieces[toRow][toCol] == null) {
            pieces[fromRow][toCol] = null;
        }

        pieces[toRow][toCol] = moving;
        pieces[fromRow][fromCol] = null;
        moved[toRow][toCol] = true;

        if (moving == Piece.WHITE_PAWN && toRow == 0) {
            pieces[toRow][toCol] = Piece.WHITE_QUEEN;
        } else if (moving == Piece.BLACK_PAWN && toRow == 7) {
            pieces[toRow][toCol] = Piece.BLACK_QUEEN;
        }

        lastMove = new Move(fromRow, fromCol, toRow, toCol);
    }

    public void reset() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                pieces[r][c] = null;
                moved[r][c] = false;
            }
        }
        lastMove = null;

        pieces[0][0] = Piece.BLACK_ROOK;
        pieces[0][1] = Piece.BLACK_KNIGHT;
        pieces[0][2] = Piece.BLACK_BISHOP;
        pieces[0][3] = Piece.BLACK_QUEEN;
        pieces[0][4] = Piece.BLACK_KING;
        pieces[0][5] = Piece.BLACK_BISHOP;
        pieces[0][6] = Piece.BLACK_KNIGHT;
        pieces[0][7] = Piece.BLACK_ROOK;
        for (int i = 0; i < 8; i++) pieces[1][i] = Piece.BLACK_PAWN;

        pieces[7][0] = Piece.WHITE_ROOK;
        pieces[7][1] = Piece.WHITE_KNIGHT;
        pieces[7][2] = Piece.WHITE_BISHOP;
        pieces[7][3] = Piece.WHITE_QUEEN;
        pieces[7][4] = Piece.WHITE_KING;
        pieces[7][5] = Piece.WHITE_BISHOP;
        pieces[7][6] = Piece.WHITE_KNIGHT;
        pieces[7][7] = Piece.WHITE_ROOK;
        for (int i = 0; i < 8; i++) pieces[6][i] = Piece.WHITE_PAWN;
    }

}