package io.arsh.ai;

import io.arsh.Config;
import io.arsh.Network;
import io.arsh.game.Rules;
import io.arsh.game.State;
import io.arsh.ui.Texture;

import java.util.ArrayList;
import java.util.List;

public class AI {

    private final Network network;

    public AI(Network network) {
        this.network = network;
    }

    public int[] chooseMove(State state, boolean isWhite) {
        double bestScore = Double.NEGATIVE_INFINITY;
        int[] bestMove = null;

        List<int[]> allLegalMoves = new ArrayList<>();

        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                Texture piece = state.getPiece(r, c);
                if (piece != null && piece.name().startsWith(isWhite ? "WHITE" : "BLACK")) {
                    var moves = Rules.getLegalMoves(state, r, c);
                    for (int[] move : moves) {
                        allLegalMoves.add(new int[]{r, c, move[0], move[1]});

                        State copy = new State(state);
                        copy.movePiece(r, c, move[0], move[1]);

                        double[] input = Encoder.encode(copy);
                        double[] out = network.forward(input);

                        double score = out[0];

                        if (Double.isFinite(score) && score > bestScore) {
                            bestScore = score;
                            bestMove = new int[]{r, c, move[0], move[1]};
                        }
                    }
                }
            }
        }

        if (bestMove == null && !allLegalMoves.isEmpty()) {
            bestMove = allLegalMoves.get((int) (Math.random() * allLegalMoves.size()));
        }

        return bestMove;
    }

    public Network getNetwork() {
        return network;
    }


}
