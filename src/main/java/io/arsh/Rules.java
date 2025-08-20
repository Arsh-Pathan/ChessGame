package io.arsh;

import java.util.ArrayList;
import java.util.List;

public class Rules {

    public static List<int[]> getLegalMoves(State state, int row, int col) {
        List<int[]> moves = getValidMoves(state, row, col);
        List<int[]> legalMoves = new ArrayList<>();

        Texture piece = state.getPiece(row, col);
        if (piece == null) return legalMoves;

        boolean isWhite = piece.name().startsWith("WHITE");

        for (int[] move : moves) {
            State copy = new State(state);
            copy.movePiece(row, col, move[0], move[1]);

            if (!isInCheck(copy, isWhite)) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    public static boolean isInCheck(State state, boolean whiteKing) {
        int kingRow = -1, kingCol = -1;

        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                Texture piece = state.getPiece(r, c);
                if (piece != null && piece.name().equals((whiteKing ? "WHITE" : "BLACK") + "_KING")) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
        }

        if (kingRow == -1) return false;

        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                Texture piece = state.getPiece(r, c);
                if (piece != null && piece.name().startsWith(whiteKing ? "BLACK" : "WHITE")) {
                    List<int[]> moves = getValidMoves(state, r, c);
                    for (int[] move : moves) {
                        if (move[0] == kingRow && move[1] == kingCol) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static List<int[]> getValidMoves(State state, int row, int col) {
        List<int[]> moves = new ArrayList<>();
        Texture piece = state.getPiece(row, col);

        if (piece == null) return moves;

        String name = piece.name();

        if (name.contains("PAWN")) {
            addPawnMoves(state, row, col, moves, name.startsWith("WHITE"));
        } else if (name.contains("ROOK")) {
            addRookMoves(state, row, col, moves);
        } else if (name.contains("KNIGHT")) {
            addKnightMoves(state, row, col, moves);
        } else if (name.contains("BISHOP")) {
            addBishopMoves(state, row, col, moves);
        } else if (name.contains("QUEEN")) {
            addRookMoves(state, row, col, moves);
            addBishopMoves(state, row, col, moves);
        } else if (name.contains("KING")) {
            addKingMoves(state, row, col, moves);
        }

        return moves;
    }

    private static void addPawnMoves(State state, int row, int col, List<int[]> moves, boolean isWhite) {
        int direction = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;

        if (isEmpty(state, row + direction, col)) {
            moves.add(new int[]{row + direction, col});

            if (row == startRow && isEmpty(state, row + 2 * direction, col)) {
                moves.add(new int[]{row + 2 * direction, col});
            }
        }

        addCapture(state, moves, row + direction, col - 1, isWhite);
        addCapture(state, moves, row + direction, col + 1, isWhite);
    }

    private static void addRookMoves(State state, int row, int col, List<int[]> moves) {
        addSlidingMoves(state, row, col, moves, new int[][]{
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        });
    }

    private static void addBishopMoves(State state, int row, int col, List<int[]> moves) {
        addSlidingMoves(state, row, col, moves, new int[][]{
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        });
    }

    private static void addKnightMoves(State state, int row, int col, List<int[]> moves) {
        int[][] deltas = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
        Texture piece = state.getPiece(row, col);
        boolean isWhite = piece.name().startsWith("WHITE");

        for (int[] d : deltas) {
            int r = row + d[0], c = col + d[1];
            if (inBounds(r, c)) {
                Texture target = state.getPiece(r, c);
                if (target == null || isWhite != target.name().startsWith("WHITE")) {
                    moves.add(new int[]{r, c});
                }
            }
        }
    }

    private static void addKingMoves(State state, int row, int col, List<int[]> moves) {
        int[][] deltas = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };
        Texture piece = state.getPiece(row, col);
        boolean isWhite = piece.name().startsWith("WHITE");

        for (int[] d : deltas) {
            int r = row + d[0], c = col + d[1];
            if (inBounds(r, c)) {
                Texture target = state.getPiece(r, c);
                if (target == null || isWhite != target.name().startsWith("WHITE")) {
                    moves.add(new int[]{r, c});
                }
            }
        }
    }

    private static void addSlidingMoves(State state, int row, int col, List<int[]> moves, int[][] directions) {
        Texture piece = state.getPiece(row, col);
        boolean isWhite = piece.name().startsWith("WHITE");

        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];
            while (inBounds(r, c)) {
                Texture target = state.getPiece(r, c);
                if (target == null) {
                    moves.add(new int[]{r, c});
                } else {
                    if (isWhite != target.name().startsWith("WHITE")) {
                        moves.add(new int[]{r, c});
                    }
                    break;
                }
                r += dir[0];
                c += dir[1];
            }
        }
    }

    private static boolean inBounds(int row, int col) {
        return row >= 0 && row < Config.BOARD_SIZE && col >= 0 && col < Config.BOARD_SIZE;
    }

    private static boolean isEmpty(State state, int row, int col) {
        return inBounds(row, col) && state.getPiece(row, col) == null;
    }

    private static void addCapture(State state, List<int[]> moves, int row, int col, boolean isWhite) {
        if (inBounds(row, col)) {
            Texture target = state.getPiece(row, col);
            if (target != null && isWhite != target.name().startsWith("WHITE")) {
                moves.add(new int[]{row, col});
            }
        }
    }

}
