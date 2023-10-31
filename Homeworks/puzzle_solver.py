import math
import time

import numpy as np


class Problema1:

    def __init__(self, initial_state):
        if len(initial_state) != 9:
            raise ValueError("Initial state should contain 9 elements.")
        self.board = np.array(initial_state).reshape(3, 3)
        self.last_moved = -1

    @staticmethod
    def initialize_state(initial_state):
        return Problema1(initial_state)

    @staticmethod
    def has_neighbor_empty_cell(matrix, row, col):
        return Problema1.is_valid(matrix, row - 1, col) or \
            Problema1.is_valid(matrix, row + 1, col) or \
            Problema1.is_valid(matrix, row, col - 1) or \
            Problema1.is_valid(matrix, row, col + 1)

    @staticmethod
    def is_valid(matrix, row, col):
        return 0 <= row < 3 and 0 <= col < 3 and matrix[row, col] == 0

    def print_state(self):
        for row in range(3):
            for col in range(3):
                print(self.board[row, col], end=" ")
            print()

    def is_final_state(self):
        expected_value = "12345678"
        value = "".join(str(self.board[row, col]) for row in range(3) for col in range(3) if self.board[row, col] != 0)
        return expected_value == value

    def move_cell(self, moved_cell):
        moved_cell_row, moved_cell_col = np.where(self.board == moved_cell)
        moved_cell_row, moved_cell_col = moved_cell_row[0], moved_cell_col[0]

        empty_cell_row, empty_cell_col = np.where(self.board == 0)
        empty_cell_row, empty_cell_col = empty_cell_row[0], empty_cell_col[0]

        if self.last_moved != moved_cell and self.has_neighbor_empty_cell(self.board, moved_cell_row, moved_cell_col):
            self.board[empty_cell_row, empty_cell_col] = moved_cell
            self.board[moved_cell_row, moved_cell_col] = 0
            self.last_moved = moved_cell
            #self.print_state()
            #print()
            return True

        return False

    def transform_matrix_to_array(self):
        return self.board.flatten()


def iddfs(initial_state):
    max_depth = 50
    local_moves = [0]
    for depth in range(max_depth + 1):
        visited = set()
        result = depth_limited_dfs(initial_state, 0, depth, visited, local_moves)
        if result:
            return result, local_moves[0]
    return None, None


def depth_limited_dfs(local_state, depth, max_depth, visited, local_moves):
    if depth == max_depth:
        if local_state.is_final_state():
            return local_state
        else:
            return None

    for i in range(9):
        original_array = local_state.transform_matrix_to_array()
        copy = Problema1.initialize_state(original_array)
        if copy.move_cell(i):
            neighbor = str(copy.transform_matrix_to_array())
            if neighbor not in visited:
                local_moves[0] += 1
                visited.add(neighbor)
                result = depth_limited_dfs(copy, depth + 1, max_depth, visited, local_moves)
                if result:
                    return result

    return None


def greedy(init_state, heuristic_function):
    priority_list = [(heuristic_function(init_state), init_state, 0)]
    visited = set()
    visited.add(str(init_state.transform_matrix_to_array()))

    while priority_list:
        priority_list.sort(key=lambda x: x[0])
        _, local_state, local_moves = priority_list.pop(0)

        if local_state.is_final_state():
            return local_state, local_moves

        for i in range(9):
            original_array = local_state.transform_matrix_to_array()
            copy = Problema1.initialize_state(original_array)
            if copy.move_cell(i):
                neighbor = str(copy.transform_matrix_to_array())
                if neighbor not in visited:
                    print(neighbor)
                    print()
                    priority_list.append((heuristic_function(copy), copy, local_moves + 1))
                    visited.add(neighbor)

    return None, None


def manhattan_distance(local_state):
    distance = 0
    for row in range(3):
        for col in range(3):
            value = local_state.board[row, col]
            if value != 0:
                goal_row = (value - 1) // 3
                goal_col = (value - 1) % 3
                distance += abs(row - goal_row) + abs(col - goal_col)
    return distance


def hamming_distance(local_state):
    distance = 0
    for row in range(3):
        for col in range(3):
            if local_state.board[row, col] != 0 and local_state.board[row, col] != row * 3 + col + 1:
                distance += 1
    return distance


def euclidean_distance(local_state):
    distance = 0
    for row in range(3):
        for col in range(3):
            value = local_state.board[row, col]
            if value != 0:
                goal_row = (value - 1) // 3
                goal_col = (value - 1) % 3
                distance += math.sqrt((row - goal_row) ** 2 + (col - goal_col) ** 2)
    return distance


if __name__ == "__main__":
    initial_state1 = [8, 6, 7, 2, 5, 4, 0, 3, 1]
    initial_state2 = [2, 5, 3, 1, 0, 6, 4, 7, 8]
    initial_state3 = [2, 7, 5, 0, 8, 4, 3, 1, 6]
    state = Problema1.initialize_state(initial_state2)
    state.print_state()
    print()

    time.sleep(20)

    # iddfs
    start_time = time.time()
    iddfs_result, iddfs_moves = iddfs(state)
    end_time = time.time()
    elapsed_time = end_time - start_time
    print(f"IDDFS Result:")
    if iddfs_result:
        iddfs_result.print_state()
        print(f"Solution Length: {iddfs_moves} moves")
    else:
        print("No solution found.")
    print(f"Time taken: {elapsed_time:.4f} seconds\n")

    # greedy manhattan
    start_time = time.time()
    result_state, moves = greedy(state, manhattan_distance)
    end_time = time.time()
    elapsed_time = end_time - start_time
    print(f"Greedy with manhattan_distance Heuristic Result:")
    if result_state:
        result_state.print_state()
        print(f"Solution Length: {moves} moves")
    else:
        print("No solution found.")
    print(f"Time taken: {elapsed_time:.4f} seconds\n")

    # greedy hamming
    start_time = time.time()
    result_state, moves = greedy(state, hamming_distance)
    end_time = time.time()
    elapsed_time = end_time - start_time
    print(f"Greedy with hamming_distance Heuristic Result:")
    if result_state:
        result_state.print_state()
        print(f"Solution Length: {moves} moves")
    else:
        print("No solution found.")
    print(f"Time taken: {elapsed_time:.4f} seconds\n")

    # greedy euclidean
    start_time = time.time()
    result_state, moves = greedy(state, euclidean_distance)
    end_time = time.time()
    elapsed_time = end_time - start_time
    print(f"Greedy with euclidean_distance Heuristic Result:")
    if result_state:
        result_state.print_state()
        print(f"Solution Length: {moves} moves")
    else:
        print("No solution found.")
    print(f"Time taken: {elapsed_time:.4f} seconds\n")
