package io.arsh.game.models;

public class Move {

    public final int fromRow, fromCol, toRow, toCol;

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    public Move(Move other) {
        this.fromRow = other.fromRow;
        this.fromCol = other.fromCol;
        this.toRow = other.toRow;
        this.toCol = other.toCol;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getToRow() {
        return toRow;
    }

    public int getToCol() {
        return toCol;
    }

}