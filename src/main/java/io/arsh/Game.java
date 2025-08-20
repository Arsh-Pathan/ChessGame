package io.arsh;

import io.arsh.ai.AI;
import io.arsh.game.Board;
import io.arsh.game.State;
import io.arsh.ui.Controls;

import javax.swing.*;
import java.awt.*;

public class Game {
    State state;
    static Board board;
    Network network;
    static Controls controller;

    public Game() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);

             state = new State();
             board = new Board(state);

            this.network = Network.load("models/ai.model");
            if (network == null) {
                network = new Network(64, 128, 64, 1);
            }
            AI ai = new AI(network);
            controller = new Controls(board, state, ai);

            board.addMouseListener(controller);

            JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 65));
            container.add(board);

            frame.add(container);
            frame.setVisible(true);

            new Timer(16, _ -> board.repaint()).start();
        });
    }

    public static void main(String[] args) {
        new Game();
    }

}
