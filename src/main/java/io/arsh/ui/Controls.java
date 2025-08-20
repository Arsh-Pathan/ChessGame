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
    private boolean aiVsAiMode = false;

    private int totalGames = 0;
    private int whiteWins = 0;
    private int blackWins = 0;
    private int ties = 0;

    public Controls(Board board, State state, AI ai) {
        this.board = board;
        this.state = state;
        this.ai = ai;
    }

    public void setAiVsAiMode(boolean enabled) {
        this.aiVsAiMode = enabled;
        if (enabled) runAIVsAIAutoRestart();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gameOver) {
            resetGame();
            if (aiVsAiMode) runAIVsAIAutoRestart();
            return;
        }

        if (aiVsAiMode) return; // ignore human input in AI vs AI mode

        int col = (e.getX() - Config.MARGIN) / Config.TILE_SIZE;
        int row = (e.getY() - Config.MARGIN) / Config.TILE_SIZE;
        makeMoveOrSelect(row, col);
    }

    public void makeMoveOrSelect(int row, int col) {
        if (col < 0 || col >= Config.BOARD_SIZE || row < 0 || row >= Config.BOARD_SIZE) return;

        if (selectedRow == -1 && state.getPiece(row, col) != null) {
            boolean isWhitePiece = state.getPiece(row, col).name().startsWith("WHITE");
            if ((whiteTurn && isWhitePiece) || (!whiteTurn && !isWhitePiece)) {
                selectedRow = row;
                selectedCol = col;
                board.setHighlightedMoves(Rules.getLegalMoves(state, row, col));
                board.setSelectedTile(row, col);
                return;
            }
        } else if (selectedRow != -1) {
            for (int[] move : Rules.getLegalMoves(state, selectedRow, selectedCol)) {
                if (move[0] == row && move[1] == col) {
                    if (!tryExecuteMove(selectedRow, selectedCol, row, col)) return;
                    break;
                }
            }
            selectedRow = -1;
            selectedCol = -1;
            board.clearSelectedTile();
            board.clearHighlightedMoves();
        }

        board.repaint();
    }

    private boolean tryExecuteMove(int fromRow, int fromCol, int toRow, int toCol) {
        Texture movingPiece = state.getPiece(fromRow, fromCol);
        Texture targetPiece = state.getPiece(toRow, toCol);

        if (targetPiece != null) {
            boolean movingIsWhite = movingPiece.name().startsWith("WHITE");
            boolean targetIsWhite = targetPiece.name().startsWith("WHITE");
            if (movingIsWhite == targetIsWhite) return false;
        }

        state.movePiece(fromRow, fromCol, toRow, toCol);
        handlePawnPromotion(toRow, toCol, movingPiece);
        history.add(new State(state));

        whiteTurn = !whiteTurn;

        checkGameOver();

        if (!gameOver && !aiVsAiMode) {
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

                state.setPiece(row, col, promotionPieces[new Random().nextInt(promotionPieces.length)]);
            }
        }
    }

    private void makeAIMove() {
        if (!hasAnyLegalMoves(false)) return;

        int[] move = ai.chooseMove(getNormalizedState(state, whiteTurn), true);
        if (move != null) {
            Texture movingPiece = state.getPiece(move[0], move[1]);
            state.movePiece(move[0], move[1], move[2], move[3]);
            handlePawnPromotion(move[2], move[3], movingPiece);
            history.add(new State(state));
            board.repaint();
        }
    }

    public State getNormalizedState(State state, boolean aiIsWhite) {
        if (aiIsWhite) return new State(state);

        State flipped = new State();
        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                Texture piece = state.getPiece(Config.BOARD_SIZE - 1 - r, c);
                if (piece != null) {
                    flipped.setPiece(r, c, flipColor(piece));
                }
            }
        }
        return flipped;
    }

    private Texture flipColor(Texture piece) {
        String name = piece.name();
        if (name.startsWith("WHITE")) return Texture.valueOf(name.replace("WHITE", "BLACK"));
        if (name.startsWith("BLACK")) return Texture.valueOf(name.replace("BLACK", "WHITE"));
        return piece;
    }

    private void runAIVsAIAutoRestart() {
        new Thread(() -> {
            while (aiVsAiMode) {
                while (!gameOver) {
                    int[] move = ai.chooseMove(state, whiteTurn);

                    if (move != null) {
                        Texture movingPiece = state.getPiece(move[0], move[1]);
                        state.movePiece(move[0], move[1], move[2], move[3]);
                        handlePawnPromotion(move[2], move[3], movingPiece);
                        history.add(new State(state));

                        whiteTurn = !whiteTurn;
                        board.repaint();

                        checkGameOver();

                        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                    } else {
                        checkGameOver();
                    }
                }

                try { Thread.sleep(100); } catch (InterruptedException ignored) {}

                resetGame();
            }
        }).start();
    }

    private int movesWithoutCapture = 0;

    private void checkGameOver() {
        if (gameOver) return;

        boolean whiteHasMoves = hasAnyLegalMoves(true);
        boolean blackHasMoves = hasAnyLegalMoves(false);

        boolean aiWon = false;

        if (!whiteHasMoves && !blackHasMoves) {
            board.setGameResult(Texture.TITLE_TIE);
            ties++;
            gameOver = true;
        } else if (!whiteHasMoves) {
            board.setGameResult(Texture.TITLE_BLACK_WINS);
            blackWins++;
            gameOver = true;
            aiWon = true;
        } else if (!blackHasMoves) {
            board.setGameResult(Texture.TITLE_WHITE_WINS);
            whiteWins++;
            gameOver = true;
        }

        int pieceCount = 0;
        boolean capturedThisMove;
        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                if (state.getPiece(r, c) != null) pieceCount++;
            }
        }

        if (!gameOver) {
            if (pieceCount <= 2) {
                board.setGameResult(Texture.TITLE_TIE);
                ties++;
                gameOver = true;
            } else {
                capturedThisMove = history.size() >= 2 && !comparePieceCounts(history.get(history.size() - 2), state);
                if (capturedThisMove) movesWithoutCapture = 0;
                else movesWithoutCapture++;

                if (movesWithoutCapture >= 50) {
                    board.setGameResult(Texture.TITLE_TIE);
                    ties++;
                    gameOver = true;
                }
            }
        }

        if (gameOver) {
            totalGames++;
            System.out.println("Game " + totalGames + " Result: " + board.getGameResult());
            System.out.println("Total games: " + totalGames + ", White Wins: " + whiteWins + ", Black Wins: " + blackWins + ", Ties: " + ties);

            Trainer.train(ai.getNetwork(), history, aiWon);
            try { ai.getNetwork().save("models/ai.model"); }
            catch (IOException e) { throw new RuntimeException(e); }
            history.clear();
            movesWithoutCapture = 0;
        }
    }

    private boolean comparePieceCounts(State oldState, State newState) {
        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                if (oldState.getPiece(r, c) != newState.getPiece(r, c)) return false;
            }
        }
        return true;
    }

    private boolean hasAnyLegalMoves(boolean white) {
        for (int r = 0; r < Config.BOARD_SIZE; r++) {
            for (int c = 0; c < Config.BOARD_SIZE; c++) {
                Texture piece = state.getPiece(r, c);
                if (piece != null && piece.name().startsWith(white ? "WHITE" : "BLACK")) {
                    if (!Rules.getLegalMoves(state, r, c).isEmpty()) return true;
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
    }
}
