package io.arsh;

import javax.swing.*;
import java.awt.*;

public class Game {

    public Game() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);

            State state = new State();
            Board board = new Board(state);
            Controls controller = new Controls(board, state);

            board.addMouseListener(controller);

            JPanel container = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 65));
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
