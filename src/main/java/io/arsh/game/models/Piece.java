package io.arsh.game.models;

import io.arsh.ui.Texture;

public enum Piece {

    BLACK_PAWN(Texture.BLACK_PAWN),
    BLACK_ROOK(Texture.BLACK_ROOK),
    BLACK_KNIGHT(Texture.BLACK_KNIGHT),
    BLACK_BISHOP(Texture.BLACK_BISHOP),
    BLACK_QUEEN(Texture.BLACK_QUEEN),
    BLACK_KING(Texture.BLACK_KING),

    WHITE_PAWN(Texture.WHITE_PAWN),
    WHITE_ROOK(Texture.WHITE_ROOK),
    WHITE_KNIGHT(Texture.WHITE_KNIGHT),
    WHITE_BISHOP(Texture.WHITE_BISHOP),
    WHITE_QUEEN(Texture.WHITE_QUEEN),
    WHITE_KING(Texture.WHITE_KING);

    private final Texture texture;

    Piece(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

}
