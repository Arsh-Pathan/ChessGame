package io.arsh;

import io.arsh.game.Board;
import io.arsh.game.models.Piece;
import io.arsh.ui.Controls;
import io.arsh.ui.Texture;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    private final Board board;
    private final int PANEL_SIZE;
    private final int TILE_SIZE;
    private final int MARGIN;
    private Texture title = null;

    private final Texture[][] tiles = new Texture[8][8];

    public Panel(double scale) {
        this.board = new Board();
        this.TILE_SIZE = (int) (64 * scale);
        this.MARGIN = (int) (16 * scale);
        this.PANEL_SIZE = TILE_SIZE * 8 + MARGIN * 2;

        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        Controls controls = new Controls(this);
        addMouseListener(controls);
    }

    @Override
    protected void paintComponent(Graphics graph) {
        super.paintComponent(graph);
        graph.drawImage(Texture.BOARD.getImage(), 0, 0, PANEL_SIZE, PANEL_SIZE, this);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int x = MARGIN + col * TILE_SIZE;
                int y = MARGIN + row * TILE_SIZE;

                Texture tile = tiles[row][col];
                if (tile == null) {
                    boolean even = (row + col) % 2 == 0;
                    tile = even ? Texture.TILE_WHITE : Texture.TILE_BLACK;
                }
                graph.drawImage(tile.getImage(), x, y, TILE_SIZE, TILE_SIZE, this);

                Piece piece = board.getPiece(row, col);
                if (piece != null) {
                    graph.drawImage(piece.getTexture().getImage(), x, y, TILE_SIZE, TILE_SIZE, this);
                }
            }
        }
        if (title != null) {
            double imgWidth = 0.6;
            double imgHeight = 0.18;

            int drawWidth = (int) (PANEL_SIZE * imgWidth) * 3 / 2;
            int drawHeight = (int) (PANEL_SIZE * imgHeight) * 3 / 2;

            int x = (PANEL_SIZE - drawWidth) / 2;
            int y = (PANEL_SIZE - drawHeight) / 2;

            graph.drawImage(title.getImage(), x, y, drawWidth, drawHeight, this);
        }
    }

    public void setTitle(Texture title) {
        this.title = title;
        repaint();
    }

    public int getTileSize() {
        return TILE_SIZE;
    }

    public int getMargin() {
        return MARGIN;
    }

    public void setTile(int row, int col, Texture texture) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) return;
        tiles[row][col] = texture;
        repaint();
    }

    public void resetTile(int row, int col) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8) return;
        tiles[row][col] = null;
        repaint();
    }

    public Board getBoard() {
        return board;
    }

}