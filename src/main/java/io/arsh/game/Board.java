package io.arsh.game;

import io.arsh.Config;
import io.arsh.ui.Texture;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Board extends JPanel {

    public State state;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private Texture gameResult = null;

    private List<int[]> highlightedMoves = new ArrayList<>();

    public Board(State state) {
        this.state = state;
        setPreferredSize(new Dimension(Config.PANEL_SIZE, Config.PANEL_SIZE));
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setGameResult(Texture result) {
        this.gameResult = result;
    }

    public void setSelectedTile(int row, int col) {
        this.selectedRow = row;
        this.selectedCol = col;
    }

    public void clearSelectedTile() {
        this.selectedRow = -1;
        this.selectedCol = -1;
    }

    public void setHighlightedMoves(List<int[]> moves) {
        this.highlightedMoves = moves;
    }

    public void clearHighlightedMoves() {
        this.highlightedMoves.clear();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(Texture.BOARD.getImage(), 0, 0, Config.PANEL_SIZE, Config.PANEL_SIZE, this);

        int[] whiteKing = findKing("WHITE_KING");
        int[] blackKing = findKing("BLACK_KING");

        boolean whiteCheck = Rules.isInCheck(state, true);
        boolean blackCheck = Rules.isInCheck(state, false);

        for (int row = 0; row < Config.BOARD_SIZE; row++) {
            for (int col = 0; col < Config.BOARD_SIZE; col++) {
                int x = Config.MARGIN + col * Config.TILE_SIZE;
                int y = Config.MARGIN + row * Config.TILE_SIZE;

                boolean isWhite = (row + col) % 2 == 0;
                Texture tile = isWhite ? Texture.TILE_WHITE : Texture.TILE_BLACK;
                g.drawImage(tile.getImage(), x, y, Config.TILE_SIZE, Config.TILE_SIZE, this);

                if ((whiteCheck && whiteKing != null && row == whiteKing[0] && col == whiteKing[1]) ||
                        (blackCheck && blackKing != null && row == blackKing[0] && col == blackKing[1])) {
                    g.drawImage(Texture.TILE_OPTION.getImage(), x, y, Config.TILE_SIZE, Config.TILE_SIZE, this);
                }

                if (row == selectedRow && col == selectedCol) {
                    g.drawImage(Texture.TILE_SELECT.getImage(), x, y, Config.TILE_SIZE, Config.TILE_SIZE, this);
                }

                for (int[] move : highlightedMoves) {
                    if (move[0] == row && move[1] == col) {
                        if (state.getPiece(row, col) == null) {
                            g.drawImage(Texture.TILE_PATH.getImage(), x, y, Config.TILE_SIZE, Config.TILE_SIZE, this);
                        } else {
                            g.drawImage(Texture.TILE_OPTION.getImage(), x, y, Config.TILE_SIZE, Config.TILE_SIZE, this);
                        }
                    }
                }

                Texture piece = state.getPiece(row, col);
                if (piece != null) {
                    g.drawImage(piece.getImage(), x, y, Config.TILE_SIZE, Config.TILE_SIZE, this);
                }
            }
        }

        if (gameResult != null) {
            int imgWidth = 600;
            int imgHeight = 180;
            int x = (getWidth() - imgWidth) / 2;
            int y = (getHeight() - imgHeight) / 2;
            g.drawImage(gameResult.getImage(), x, y, imgWidth, imgHeight, this);
        }
    }


    private int[] findKing(String kingName) {
        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                Texture piece = state.getPiece(r, c);
                if (piece != null && piece.name().equals(kingName)) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }


}
