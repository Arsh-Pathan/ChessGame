package io.arsh.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public enum Texture {
    BOARD("assets/board.png"),

    TITLE_WHITE_WINS("assets/titles/white_win.png"),
    TITLE_BLACK_WINS("assets/titles/black_win.png"),
    TITLE_TIE("assets/titles/tie.png"),

    TILE_BLACK("assets/tiles/black_tile.png"),
    TILE_WHITE("assets/tiles/white_tile.png"),
    TILE_OPTION("assets/tiles/option_tile.png"),
    TILE_PATH("assets/tiles/path_tile.png"),
    TILE_SELECT("assets/tiles/select_tile.png"),

    BLACK_PAWN("assets/pieces/black/black_pawn.png"),
    BLACK_ROOK("assets/pieces/black/black_rook.png"),
    BLACK_KNIGHT("assets/pieces/black/black_knight.png"),
    BLACK_BISHOP("assets/pieces/black/black_bishop.png"),
    BLACK_QUEEN("assets/pieces/black/black_queen.png"),
    BLACK_KING("assets/pieces/black/black_king.png"),

    WHITE_PAWN("assets/pieces/white/white_pawn.png"),
    WHITE_ROOK("assets/pieces/white/white_rook.png"),
    WHITE_KNIGHT("assets/pieces/white/white_knight.png"),
    WHITE_BISHOP("assets/pieces/white/white_bishop.png"),
    WHITE_QUEEN("assets/pieces/white/white_queen.png"),
    WHITE_KING("assets/pieces/white/white_king.png");

    private BufferedImage image;

    Texture(String path) {
        try {
            image = ImageIO.read(
                    Objects.requireNonNull(Texture.class.getClassLoader().getResourceAsStream(path))
            );
        } catch (IOException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    public BufferedImage getImage() {
        return image;
    }

}
