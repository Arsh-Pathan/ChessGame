package io.arsh.game;

import io.arsh.game.models.Move;
import io.arsh.game.models.Piece;

import java.util.ArrayList;
import java.util.List;

public class Rules {

    public static List<Move> getAllLegalMoves(Board board, boolean white) {
        List<Move> allMoves = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.isWhite() == white) {
                    allMoves.addAll(getLegalMoves(board, r, c));
                }
            }
        }
        return allMoves;
    }

    public static List<Move> getLegalMoves(Board board, int row, int col) {
        List<Move> pseudoMoves = getValidMoves(board, row, col);
        List<Move> legalMoves = new ArrayList<>();

        Piece piece = board.getPiece(row, col);
        if (piece == null) return legalMoves;

        boolean white = piece.isWhite();

        for (Move move : pseudoMoves) {
            Board copy = new Board(board);
            copy.movePiece(row, col, move.toRow, move.toCol);
            if (!isInCheck(copy, white)) {
                legalMoves.add(move);
            }
        }

        if (piece.name().contains("KING") && !board.hasMoved(row, col) && !isInCheck(board, white)) {
            if (!board.hasMoved(row, 7) && isEmpty(board, row, 5) && isEmpty(board, row, 6)) {
                if (!isSquareAttacked(board, row, 5, !white) && !isSquareAttacked(board, row, 6, !white)) {
                    legalMoves.add(new Move(row, col, row, 6));
                }
            }
            if (!board.hasMoved(row, 0) && isEmpty(board, row, 1) && isEmpty(board, row, 2) && isEmpty(board, row, 3)) {
                if (!isSquareAttacked(board, row, 2, !white) && !isSquareAttacked(board, row, 3, !white)) {
                    legalMoves.add(new Move(row, col, row, 2));
                }
            }
        }

        return legalMoves;
    }

    public static boolean isInCheck(Board board, boolean whiteKing) {
        int kingRow = -1, kingCol = -1;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getPiece(r, c);
                if (p == (whiteKing ? Piece.WHITE_KING : Piece.BLACK_KING)) {
                    kingRow = r;
                    kingCol = c;
                }
            }
        }
        if (kingRow == -1) return false;

        return isSquareAttacked(board, kingRow, kingCol, !whiteKing);
    }

    private static boolean isSquareAttacked(Board board, int row, int col, boolean byWhite) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece attacker = board.getPiece(r, c);
                if (attacker != null && attacker.isWhite() == byWhite) {
                    for (Move move : getValidMoves(board, r, c)) {
                        if (move.toRow == row && move.toCol == col) return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<Move> getValidMoves(Board board, int row, int col) {
        List<Move> moves = new ArrayList<>();
        Piece piece = board.getPiece(row, col);
        if (piece == null) return moves;

        String name = piece.name();

        if (name.contains("PAWN")) {
            addPawnMoves(board, row, col, moves, piece.isWhite());
        } else if (name.contains("ROOK")) {
            addSlidingMoves(board, row, col, moves, new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}});
        } else if (name.contains("BISHOP")) {
            addSlidingMoves(board, row, col, moves, new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}});
        } else if (name.contains("QUEEN")) {
            addSlidingMoves(board, row, col, moves, new int[][]{
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}});
        } else if (name.contains("KNIGHT")) {
            addKnightMoves(board, row, col, moves);
        } else if (name.contains("KING")) {
            addKingMoves(board, row, col, moves);
        }
        return moves;
    }

    private static void addPawnMoves(Board board, int row, int col, List<Move> moves, boolean white) {
        int dir = white ? -1 : 1;
        int startRow = white ? 6 : 1;

        if (isEmpty(board, row + dir, col)) {
            moves.add(new Move(row, col, row + dir, col));
            if (row == startRow && isEmpty(board, row + 2 * dir, col)) {
                moves.add(new Move(row, col, row + 2 * dir, col));
            }
        }
        addCapture(board, moves, row, col, row + dir, col - 1, white);
        addCapture(board, moves, row, col, row + dir, col + 1, white);

        Move last = board.getLastMove();
        if (last != null) {
            Piece movedPawn = board.getPiece(last.toRow, last.toCol);
            if (movedPawn != null && movedPawn.name().contains("PAWN") &&
                    Math.abs(last.toRow - last.fromRow) == 2 && last.toRow == row) {
                if (Math.abs(last.toCol - col) == 1) {
                    moves.add(new Move(row, col, row + dir, last.toCol));
                }
            }
        }
    }

    private static void addKnightMoves(Board board, int row, int col, List<Move> moves) {
        int[][] deltas = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        Piece knight = board.getPiece(row, col);
        boolean white = knight.isWhite();

        for (int[] d : deltas) {
            int r = row + d[0], c = col + d[1];
            if (inBounds(r, c)) {
                Piece t = board.getPiece(r, c);
                if (t == null || t.isWhite() != white) moves.add(new Move(row, col, r, c));
            }
        }
    }

    private static void addKingMoves(Board board, int row, int col, List<Move> moves) {
        int[][] deltas = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        Piece king = board.getPiece(row, col);
        boolean white = king.isWhite();

        for (int[] d : deltas) {
            int r = row + d[0], c = col + d[1];
            if (inBounds(r, c)) {
                Piece t = board.getPiece(r, c);
                if (t == null || t.isWhite() != white) moves.add(new Move(row, col, r, c));
            }
        }
    }

    private static void addSlidingMoves(Board board, int row, int col, List<Move> moves, int[][] dirs) {
        Piece piece = board.getPiece(row, col);
        boolean white = piece.isWhite();

        for (int[] dir : dirs) {
            int r = row + dir[0], c = col + dir[1];
            while (inBounds(r, c)) {
                Piece t = board.getPiece(r, c);
                if (t == null) {
                    moves.add(new Move(row, col, r, c));
                } else {
                    if (t.isWhite() != white) moves.add(new Move(row, col, r, c));
                    break;
                }
                r += dir[0];
                c += dir[1];
            }
        }
    }

    private static void addCapture(Board board, List<Move> moves, int fromRow, int fromCol, int row, int col, boolean white) {
        if (inBounds(row, col)) {
            Piece t = board.getPiece(row, col);
            if (t != null && t.isWhite() != white) moves.add(new Move(fromRow, fromCol, row, col));
        }
    }

    private static boolean inBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private static boolean isEmpty(Board board, int row, int col) {
        return inBounds(row, col) && board.getPiece(row, col) == null;
    }
}