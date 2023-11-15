import random

import numpy as np


class NeuralNetwork:
    def __init__(self):
        # Parameters
        self.input_size = 7
        self.hidden_layer_count = 1
        self.hidden_size = 5
        self.output_size = 3

        self.weights = []
        self.biases = []

        # Initialize weights with random values
        for input_nodes, output_nodes in zip(
                [self.input_size] + [self.hidden_size] * self.hidden_layer_count + [self.output_size],
                [self.hidden_size] * self.hidden_layer_count + [self.output_size]):
            weight_matrix = np.random.randn(input_nodes, output_nodes)
            self.weights.append(weight_matrix)

        # Initialize biases with random values
        for output_nodes in [self.hidden_size] * self.hidden_layer_count + [self.output_size]:
            bias_matrix = np.random.randn(1, output_nodes)
            self.biases.append(bias_matrix)

    def sigmoid(self, x):
        return 1 / (1 + np.exp(-x))

    def sigmoid_derivative(self, x):
        sigmoid_x = self.sigmoid(x)
        return sigmoid_x * (1 - sigmoid_x)

    def mean_square_error(self, predicted, actual):
        error = 0
        for i in range(0, len(predicted)):
            error += (predicted[i] - actual[i]) ** 2
        return error / len(predicted)

    def forward(self, input_data):

        layer_output = np.array(input_data)
        for k in range(0, self.hidden_layer_count):
            next_layer_activation = np.empty(self.hidden_size)
            sum_weights = self.biases[k]
            # print("biases de k: ", self.biases[k])
            for j in range(0, self.hidden_size):
                for i in range(0, self.input_size):
                    # print("layer output de i: ", layer_output[i])
                    sum_weights += layer_output[i] * self.weights[k][i][j]
                    # print("weights de k i j: ", self.weights[k][i][j])
                next_layer_activation[j] = self.sigmoid(sum_weights.flatten()[j])
            layer_output = next_layer_activation
        output = np.empty(self.output_size)
        for j in range(0, self.output_size):
            sum_weights = self.biases[self.hidden_layer_count]
            for i in range(0, self.hidden_size):
                sum_weights += layer_output[i] * self.weights[self.hidden_layer_count][i][j]
            output[j] = self.sigmoid(sum_weights[0, j])

        return output


class ReadInputFile:
    def __init__(self):
        self.train_data = None
        self.test_data = None

    def set_test_and_train_data(self):
        file_path = "seeds_dataset.txt"
        raw_data = self.read_data_from_file(file_path)
        random.shuffle(raw_data)
        split_index = int(0.8 * len(raw_data))
        self.train_data = self.convert_to_float_array(raw_data[:split_index])
        self.test_data = self.convert_to_float_array(raw_data[split_index:])

    def get_train_data(self):
        return self.train_data

    def get_test_data(self):
        return self.test_data

    @staticmethod
    def read_data_from_file(file_path):
        with open(file_path, 'r') as file:
            data = file.readlines()
        return data

    @staticmethod
    def convert_to_float_array(string_list):
        double_list = []
        for line in string_list:
            values = [float(value) for value in line.split()]
            double_list.extend(values)
        return np.array(double_list)


if __name__ == "__main__":
    neural_network = NeuralNetwork()
    read_input_file = ReadInputFile()
    read_input_file.set_test_and_train_data()
    test_input = read_input_file.get_test_data()
    predicted_output = neural_network.forward(test_input)
    print("Forward result: \n")
    for value in range(0, len(predicted_output)):
        print(f"{predicted_output[value]} ")
