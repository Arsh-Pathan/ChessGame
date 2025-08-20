package io.arsh.ui;

import io.arsh.Config;
import io.arsh.ai.AI;
import io.arsh.ai.Trainer;
import io.arsh.game.Board;
import io.arsh.game.Rules;
import io.arsh.game.State;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controls extends MouseAdapter {

    private final Board board;
    private State state;
    private boolean gameOver = false;

    private final AI ai;
    private final List<State> history = new ArrayList<>();

    private int selectedRow = -1;
    private int selectedCol = -1;

    private boolean whiteTurn = true;

    public Controls(Board board, State state, AI ai) {
        this.board = board;
        this.state = state;
        this.ai = ai;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameOver) {
            resetGame();
            return;
        }
        int col = (e.getX() - Config.MARGIN) / Config.TILE_SIZE;
        int row = (e.getY() - Config.MARGIN) / Config.TILE_SIZE;
        makeMoveOrSelect(row, col);
    }

    public void makeMoveOrSelect(int row, int col) {
        if (col < 0 || col >= Config.BOARD_SIZE || row < 0 || row >= Config.BOARD_SIZE) {
            return;
        }

        if (selectedRow == -1 && state.getPiece(row, col) != null) {
            Texture piece = state.getPiece(row, col);

            boolean isWhitePiece = piece.name().startsWith("WHITE");
            if ((whiteTurn && isWhitePiece) || (!whiteTurn && !isWhitePiece)) {
                selectedRow = row;
                selectedCol = col;

                List<int[]> moves = Rules.getLegalMoves(state, row, col);
                board.setHighlightedMoves(moves);
                board.setSelectedTile(row, col);
                return;
            }

        } else if (selectedRow != -1) {
            List<int[]> moves = Rules.getLegalMoves(state, selectedRow, selectedCol);

            for (int[] move : moves) {
                if (move[0] == row && move[1] == col) {
                    if (!tryExecuteMove(selectedRow, selectedCol, row, col)) {
                        return;
                    }
                    break;
                }
            }

            selectedRow = -1;
            selectedCol = -1;
            board.clearSelectedTile();
            board.clearHighlightedMoves();
        }

        checkGameOver();
        board.repaint();
    }

    private boolean tryExecuteMove(int fromRow, int fromCol, int toRow, int toCol) {
        Texture targetPiece = state.getPiece(toRow, toCol);
        Texture movingPiece = state.getPiece(fromRow, fromCol);

        if (targetPiece != null) {
            boolean movingIsWhite = movingPiece.name().startsWith("WHITE");
            boolean targetIsWhite = targetPiece.name().startsWith("WHITE");
            if (movingIsWhite == targetIsWhite) {
                return false;
            }
        }

        state.movePiece(fromRow, fromCol, toRow, toCol);
        handlePawnPromotion(toRow, toCol, movingPiece);
        history.add(new State(state));
        whiteTurn = !whiteTurn;

        checkGameOver();

        if (!gameOver && !whiteTurn) {
            makeAIMove();
            whiteTurn = true;
            checkGameOver();
        }

        return true;
    }

    private void handlePawnPromotion(int row, int col, Texture piece) {
        if (piece.name().contains("PAWN")) {
            boolean isWhite = piece.name().startsWith("WHITE");
            int promotionRow = isWhite ? 0 : 7;
            if (row == promotionRow) {
                Texture[] promotionPieces = isWhite ?
                        new Texture[]{Texture.WHITE_QUEEN, Texture.WHITE_ROOK, Texture.WHITE_BISHOP, Texture.WHITE_KNIGHT} :
                        new Texture[]{Texture.BLACK_QUEEN, Texture.BLACK_ROOK, Texture.BLACK_BISHOP, Texture.BLACK_KNIGHT};

                Random random = new Random();
                Texture newPiece = promotionPieces[random.nextInt(promotionPieces.length)];
                state.setPiece(row, col, newPiece);
            }
        }
    }

    private void makeAIMove() {
        if (!hasAnyLegalMoves(false)) {
            System.out.println("AI has no legal moves.");
            return;
        }

        int[] move = ai.chooseMove(state, false);
        if (move != null) {
            Texture movingPiece = state.getPiece(move[0], move[1]);
            state.movePiece(move[0], move[1], move[2], move[3]);
            handlePawnPromotion(move[2], move[3], movingPiece);
            history.add(new State(state));
            board.repaint();
        } else {
            System.out.println("AI could not find a valid move.");
        }
    }

    private void checkGameOver() {
        boolean whiteHasMoves = hasAnyLegalMoves(true);
        boolean blackHasMoves = hasAnyLegalMoves(false);

        boolean gameOver = false;
        boolean aiWon = false;

        if (!whiteHasMoves) {
            gameOver = true;
            board.setGameResult(Texture.TITLE_BLACK_WINS);
            aiWon = true;
        } else if (!blackHasMoves) {
            gameOver = true;
            board.setGameResult(Texture.TITLE_WHITE_WINS);
        }

        int pieceCount = 0;
        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                Texture p = state.getPiece(r, c);
                if (p != null) pieceCount++;
            }
        }
        if (pieceCount == 2) {
            board.setGameResult(Texture.TITLE_TIE);
            gameOver = true;
        }

        if (gameOver) {
            this.gameOver = true;
            Trainer.train(ai.getNetwork(), history, aiWon);
            try {
                ai.getNetwork().save("models/ai.model");
                System.out.println("Saving network progress...");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            history.clear();
        }
    }

    private boolean hasAnyLegalMoves(boolean white) {
        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                Texture piece = state.getPiece(r, c);
                if (piece != null && piece.name().startsWith(white ? "WHITE" : "BLACK")) {
                    if (!Rules.getLegalMoves(state, r, c).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void resetGame() {
        this.state = new State();
        this.board.setState(state);
        this.whiteTurn = true;
        this.history.clear();
        this.board.clearSelectedTile();
        this.board.clearHighlightedMoves();
        this.board.setGameResult(null);
        this.gameOver = false;
        this.board.repaint();
    }

}