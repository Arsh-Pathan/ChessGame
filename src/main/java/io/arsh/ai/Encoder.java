package io.arsh.ai;

import io.arsh.Config;
import io.arsh.game.State;
import io.arsh.ui.Texture;

public class Encoder {

    public static double[] encode(State state) {
        double[] input = new double[64];
        int idx = 0;
        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                Texture piece = state.getPiece(r, c);
                input[idx++] = encodePiece(piece);
            }
        }
        return input;
    }

    private static double encodePiece(Texture piece) {
        if (piece == null) return 0;
        String name = piece.name();
        return switch (name) {
            case "WHITE_PAWN" -> 1;
            case "WHITE_KNIGHT" -> 2;
            case "WHITE_BISHOP" -> 3;
            case "WHITE_ROOK" -> 4;
            case "WHITE_QUEEN" -> 5;
            case "WHITE_KING" -> 6;
            case "BLACK_PAWN" -> -1;
            case "BLACK_KNIGHT" -> -2;
            case "BLACK_BISHOP" -> -3;
            case "BLACK_ROOK" -> -4;
            case "BLACK_QUEEN" -> -5;
            case "BLACK_KING" -> -6;
            default -> 0;
        };
    }

}
