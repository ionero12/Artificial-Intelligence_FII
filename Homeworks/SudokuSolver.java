package org.example;

import java.util.Arrays;

public class SudokuSolver {

    private static final int BOARD_SIZE = 9;

    public static void main(String[] args) {
        int[][] sudokuBoard = {{8, 4, 0, 0, 5, 0, -1, 0, 0}, {3, 0, 0, 6, 0, 8, 0, 4, 0}, {0, 0, -1, 4, 0, 9, 0, 0, -1}, {0, 2, 3, 0, -1, 0, 9, 8, 0}, {1, 0, 0, -1, 0, -1, 0, 0, 4}, {0, 9, 8, 0, -1, 0, 1, 6, 0}, {-1, 0, 0, 5, 0, 3, -1, 0, 0}, {0, 3, 0, 1, 0, 6, 0, 0, 7}, {0, 0, -1, 0, 2, 0, 0, 1, 3}};
        SudokuSolver solver = new SudokuSolver();
        solver.printBoard(sudokuBoard);
        System.out.println();
        long start = System.currentTimeMillis();
        int[][] solution = solver.solveSudoku(sudokuBoard);
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + " miliseconds" + "\n");
        if (solution != null) {
            System.out.println("Sudoku Solved:");
            solver.printBoard(solution);
        } else {
            System.out.println("No solution exists.");
        }
    }

    private int[][][] createDomains(int[][] sudokuBoard) {
        int[][][] domains = new int[BOARD_SIZE][BOARD_SIZE][];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (sudokuBoard[i][j] == -1) {
                    domains[i][j] = new int[]{2, 4, 6, 8};
                } else if (sudokuBoard[i][j] == 0) {
                    domains[i][j] = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
                } else {
                    domains[i][j] = new int[]{sudokuBoard[i][j]};
                }
            }
        }
        return domains;
    }

    private int[][] solveSudoku(int[][] board) {
        int[][][] domains = createDomains(board);
        return BKT_FC_MRV(board, domains);
    }

    private int[][] BKT_FC_MRV(int[][] board, int[][][] domains) {
        if (isComplete(board)) {
            return board;
        }
        //int[] var = nextUnassignedVariable(board);
        int[] var = nextUnassignedVariableMRV(board, domains);
        int row = var[0];
        int col = var[1];
        for (int value : domains[row][col]) {
            if (isConsistent(board, row, col, value)) {
                int[][] newBoard = assignValue(board, row, col, value);
                int[][][] newDomains = updateDomainsFC(domains, row, col, value);
                if (noEmptyDomains(newDomains)) {
                    int[][] result = BKT_FC_MRV(newBoard, newDomains);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private boolean isComplete(int[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0 || board[i][j] == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[] nextUnassignedVariable(int[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0 || board[i][j] == -1) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private int[] nextUnassignedVariableMRV(int[][] board, int[][][] domains) {
        int[] varMRV = new int[2];
        int minSize = Integer.MAX_VALUE;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0 || board[i][j] == -1) {
                    int size = domains[i][j].length;
                    if (size < minSize && size > 0) {
                        minSize = size;
                        varMRV[0] = i;
                        varMRV[1] = j;
                    }
                }
            }
        }
        return varMRV;
    }

    private boolean isConsistent(int[][] board, int row, int col, int value) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][col] == value || board[row][i] == value) {
                return false;
            }
        }
        int regionRow = row / 3 * 3;
        int regionCol = col / 3 * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[regionRow + i][regionCol + j] == value) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[][][] updateDomainsFC(int[][][] domains, int row, int col, int value) {
        int[][][] newDomains = cloneDomains(domains);
        for (int i = 0; i < BOARD_SIZE; i++) {
            newDomains[i][col] = removeValueFromDomain(newDomains[i][col], value);
            newDomains[row][i] = removeValueFromDomain(newDomains[row][i], value);
        }
        int regionRow = row / 3 * 3;
        int regionCol = col / 3 * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                newDomains[regionRow + i][regionCol + j] = removeValueFromDomain(newDomains[regionRow + i][regionCol + j], value);
            }
        }
        return newDomains;
    }

    private int[][] assignValue(int[][] board, int row, int col, int value) {
        int[][] newBoard = cloneBoard(board);
        newBoard[row][col] = value;
        return newBoard;
    }

    private int[][][] cloneDomains(int[][][] domains) {
        int[][][] newDomains = new int[BOARD_SIZE][BOARD_SIZE][];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                newDomains[i][j] = Arrays.copyOf(domains[i][j], domains[i][j].length);
            }
        }
        return newDomains;
    }

    private int[][] cloneBoard(int[][] board) {
        int[][] newBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            newBoard[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return newBoard;
    }

    private int[] removeValueFromDomain(int[] domain, int value) {
        int[] newDomain = Arrays.copyOf(domain, domain.length);
        for (int i = 0; i < newDomain.length; i++) {
            if (newDomain[i] == value) {
                newDomain[i] = 0;
                break;
            }
        }
        return newDomain;
    }

    private boolean noEmptyDomains(int[][][] domains) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (domains[i][j].length == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void printBoard(int[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}
