package io.arsh.ai;

import io.arsh.Network;
import io.arsh.game.State;

import java.util.List;

public class Trainer {

    public static void train(Network network, List<State> gameStates, boolean aiWon) {
        double finalReward = aiWon ? 1.0 : 0.0;

        double gamma = 0.95;

        double reward = finalReward;
        for (int i = gameStates.size() - 1; i >= 0; i--) {
            State state = gameStates.get(i);
            double[] input = Encoder.encode(state);
            network.train(input, new double[]{reward});
            reward *= gamma;
        }
    }
}
