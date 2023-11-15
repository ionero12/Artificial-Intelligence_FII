package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TicTacToe {
    private final char[][] board;
    private final List<Integer> playerAMoves;
    private final List<Integer> computerMoves;
    private char currentPlayer;

    public TicTacToe() {
        board = new char[3][3];
        currentPlayer = 'A';
        initializeBoard();
        playerAMoves = new ArrayList<>();
        computerMoves = new ArrayList<>();
    }

    public static void main(String[] args) {
        TicTacToe game = new TicTacToe();
        game.playGame(game.board);
    }

    public int calculateOpenDirections(char[][] board, char player) {
        int playerDirections = 0;

        for (int i = 0; i < 3; i++) {
            int openRowCount = 0;
            int openColCount = 0;
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == player || board[i][j] == '-') {
                    openRowCount++;
                }
                if (board[j][i] == player || board[j][i] == '-') {
                    openColCount++;
                }
            }
            if (openRowCount == 3) {
                playerDirections += 1;
            }
            if (openColCount == 3) {
                playerDirections += 1;
            }
        }

        int openDiagonal1Count = 0;
        int openDiagonal2Count = 0;
        for (int i = 0; i < 3; i++) {
            if (board[i][i] == player || board[i][i] == '-') {
                openDiagonal1Count ++;
            }
            if (board[i][2 - i] == player || board[i][2 - i] == '-') {
                openDiagonal2Count ++;
            }
        }
        if (openDiagonal1Count == 3) {
            playerDirections += 1;
        }
        if (openDiagonal2Count == 3) {
            playerDirections += 1;
        }

        return playerDirections;
    }

    public int heuristic(char[][] board, char player) {
        int openDirectionsA = calculateOpenDirections(board, 'A');
        int openDirectionsB = calculateOpenDirections(board, 'B');

        if (player == 'A') return openDirectionsA - openDirectionsB;
        else return openDirectionsB - openDirectionsA;
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }

    private void printBoard() {
        System.out.println("-------------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-------------");
        }
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isWinner(char player) {
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == player && board[i][1] == player && board[i][2] == player) || (board[0][i] == player && board[1][i] == player && board[2][i] == player)) {
                return true;
            }
        }

        return (board[0][0] == player && board[1][1] == player && board[2][2] == player) || (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    public List<Integer> getValuePosition(int value) {
        if (value == 1) return List.of(1, 2);
        if (value == 2) return List.of(0, 0);
        if (value == 3) return List.of(2, 1);
        if (value == 4) return List.of(2, 0);
        if (value == 5) return List.of(1, 1);
        if (value == 6) return List.of(0, 2);
        if (value == 7) return List.of(0, 1);
        if (value == 8) return List.of(2, 2);
        if (value == 9) return List.of(1, 0);
        return null;
    }

    // verifica daca mutarea e valida si o executa
    private boolean makeMove(int row, int col, char player) {
        if (row >= 0 && row <= 2 && col >= 0 && col <= 2 && board[row][col] == '-') {
            board[row][col] = player;
            return true;
        }
        return false;
    }


//    private int minimax(int depth, boolean isMaximizingPlayer) {
//        int score = evaluate();
//        int count=0;
//        if (score != 2) {
//            return score;
//        }
//
//        if (isMaximizingPlayer) {
//            int maxEval = Integer.MIN_VALUE;
//            for (int i = 0; i < 3; i++) {
//                for (int j = 0; j < 3; j++) {
//                    if (board[i][j] == '-') {
//                        board[i][j] = 'B';
//                        int eval = minimax(depth + 1, false);
//                        System.out.println("Depth: " + depth + ", eval: " + eval + ", maxEval: " + maxEval + ", count: " + count);
//                        count++;
//                        board[i][j] = '-';
//                        maxEval = Math.max(maxEval, eval);
//                    }
//                }
//            }
//            return maxEval;
//        } else {
//            int minEval = Integer.MAX_VALUE;
//            for (int i = 0; i < 3; i++) {
//                for (int j = 0; j < 3; j++) {
//                    if (board[i][j] == '-') {
//                        board[i][j] = 'A';
//                        int eval = minimax(depth + 1, true);
//                        board[i][j] = '-';
//                        minEval = Math.min(minEval, eval);
//                    }
//                }
//            }
//            return minEval;
//        }
//    }

    private boolean isValidMove(int value) {
        return !playerAMoves.contains(value) && !computerMoves.contains(value);
    }

    private int evaluateGameState() {
        if (isWinner('A')) {
            return -1; // A
        } else if (isWinner('B')) {
            return 1; // B
        } else if (isBoardFull()) {
            return 0; // Remiza
        }
        return 2;
    }

    private int minimax2(char[][] board, int depth, boolean isMaximizingPlayer, char currentPlayer) {
        int gameState = evaluateGameState();
        if (depth == 2 || gameState != 2) {
            return heuristic(board, currentPlayer);
        }

        if (isMaximizingPlayer) {
            int maxScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '-') {
                        board[i][j] = 'B';
                        int score = minimax(board, depth + 1, false, 'B');
                        board[i][j] = '-';
                        maxScore = Math.max(maxScore, score);
                    }
                }
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '-') {
                        board[i][j] = 'A';
                        int score = minimax(board, depth + 1, true, 'A');
                        board[i][j] = '-';
                        minScore = Math.min(minScore, score);
                    }
                }
            }
            return minScore;
        }
    }

    private int minimax(char[][] board, int depth, boolean isMaximizingPlayer, char currentPlayer) {
        int gameState = evaluateGameState();
        if (depth == 0 || gameState != 2) {
            return heuristic(board, currentPlayer);
        }

        int value;
        if (isMaximizingPlayer) value = Integer.MIN_VALUE;
        else value = Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    if (isMaximizingPlayer) {
                        board[i][j] = 'B';
                        int score = minimax(board, depth - 1, false, 'B');
                        board[i][j] = '-';
                        value = Math.max(value, score);
                    } else {
                        board[i][j] = 'A';
                        int score = minimax(board, depth - 1, true, 'A');
                        board[i][j] = '-';
                        value = Math.min(value, score);
                    }
                }
            }
        }
        return value;
    }

    public int getValueFromCoordinates(int row, int col) {
        if (row == 1 && col == 2) return 1;
        if (row == 0 && col == 0) return 2;
        if (row == 2 && col == 1) return 3;
        if (row == 2 && col == 0) return 4;
        if (row == 1 && col == 1) return 5;
        if (row == 0 && col == 2) return 6;
        if (row == 0 && col == 1) return 7;
        if (row == 2 && col == 2) return 8;
        if (row == 1 && col == 0) return 9;
        return 0;
    }

    private void computerMove(char[][] board) {
        int bestScore = Integer.MIN_VALUE;
        int bestMoveRow = -1;
        int bestMoveCol = -1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    board[i][j] = 'B';
                    int score = minimax(board, 2, false, 'B');
                    board[i][j] = '-';

                    if (score > bestScore) {
                        bestScore = score;
                        bestMoveRow = i;
                        bestMoveCol = j;
                    }
                }
            }
        }
        System.out.println("Best h: " + bestScore);
        makeMove(bestMoveRow, bestMoveCol, 'B');
        computerMoves.add(getValueFromCoordinates(bestMoveRow, bestMoveCol));
    }

    public void playGame(char[][] board) {
        Scanner scanner = new Scanner(System.in);

        while (evaluateGameState() == 2) {
            printBoard();
            System.out.println("Player A's moves: " + playerAMoves);
            System.out.println("Player B's moves: " + computerMoves);

            if (currentPlayer == 'A') {
                // User's turn
                System.out.println("Alege un nr de la 1 la 9: ");
                int value = scanner.nextInt();
                if (value < 1 || value > 9) {
                    System.out.println("Nu ai introdus un numar intre 1 si 9. Mai incearca o data");
                    continue;
                }
                int row = getValuePosition(value).get(0);
                int col = getValuePosition(value).get(1);

                if (makeMove(row, col, 'A')) {
                    playerAMoves.add(value);
                    currentPlayer = 'B';
                } else {
                    System.out.println("Mutare invalida. Mai incearca o data.");
                }
            } else {
                // Computer's turn
                computerMove(board);
                currentPlayer = 'A';
            }
        }

        printBoard();
        int result = evaluateGameState();
        if (result == -1) {
            System.out.println("Felicitari, ai castigat!");
        } else if (result == 1) {
            System.out.println("Ai pierdut!");
        } else {
            System.out.println("Remiza!");
        }

        scanner.close();
    }

}