package io.arsh;

import io.arsh.ui.Texture;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chess");
            frame.setIconImage(Texture.WHITE_KING.getImage());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);

//            int games = 16;
//            int columns = 4;
//            double scale = 0.45;
//
//            frame.setLayout(new GridLayout(0, columns));
//
//            for (int i = 0; i < games; i++) {
//                Panel panel = new Panel(scale);
//                frame.add(panel);
//            }

            Panel panel = new Panel(1.0);
            frame.add(panel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}

