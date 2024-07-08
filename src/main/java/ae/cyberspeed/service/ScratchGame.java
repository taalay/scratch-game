package ae.cyberspeed.service;

import ae.cyberspeed.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.stream.Stream;

public class ScratchGame {

    private final Configuration config;
    private final double betAmount;
    private final Random random;

    public ScratchGame(Configuration config, double betAmount, Random random) {
        this.config = config;
        this.betAmount = betAmount;
        this.random = random;
    }

    public Result play() {
        String[][] matrix = generateMatrix();

        var winningCombinations = checkWinningCombinations(matrix);

        double reward = calculateReward(winningCombinations);

        String bonusCell = getBonusCell(matrix);

        reward = applyBonusSymbol(bonusCell, reward);

        return new Result(betAmount, matrix, reward, winningCombinations, bonusCell);
    }

    private String[][] generateMatrix() {
        String[][] matrix = new String[config.getRows()][config.getColumns()];

        for (StandardSymbolProbability prob : config.getProbabilities().getStandardSymbols()) {
            int column = prob.getColumn();
            int row = prob.getRow();
            if (column < config.getColumns() && row < config.getRows()) {
                matrix[row][column] = getRandomSymbol(prob.getSymbols());
            }
        }

        //Filling the remaining matrix cells if they are not specified in the configuration
        for (int i = 0; i < config.getRows(); i++) {
            for (int j = 0; j < config.getColumns(); j++) {
                if (matrix[i][j] == null) {
                    StandardSymbolProbability defaultProb = config.getProbabilities().getStandardSymbols().get(0);
                    matrix[i][j] = getRandomSymbol(defaultProb.getSymbols());
                }
            }
        }

        addBonusSymbols(matrix);

        return matrix;
    }

    private String getRandomSymbol(Map<String, Integer> symbols) {
        int totalWeight = symbols.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = random.nextInt(totalWeight);
        int currentWeight = 0;
        for (Map.Entry<String, Integer> entry : symbols.entrySet()) {
            currentWeight += entry.getValue();
            if (randomValue < currentWeight) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("could not get random symbol");
    }

    private void addBonusSymbols(String[][] matrix) {
        Map<String, Integer> bonusSymbols = config.getProbabilities().getBonusSymbols();
        int totalWeight = bonusSymbols.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = random.nextInt(totalWeight);
        int currentWeight = 0;
        for (Map.Entry<String, Integer> entry : bonusSymbols.entrySet()) {
            currentWeight += entry.getValue();
            if (randomValue < currentWeight) {
                int row = random.nextInt(config.getRows());
                int col = random.nextInt(config.getColumns());
                matrix[row][col] = entry.getKey();
                break;
            }
        }
    }

    private Map<String, List<String>> checkWinningCombinations(String[][] matrix) {
        Map<String, List<String>> sameSymbolWinningCombinations = new HashMap<>();
        Map<String, List<String>> linerSymbolWinningCombinations = new HashMap<>();

        for (var entry : config.getWinCombinations().entrySet()) {
            String combinationName = entry.getKey();
            var combination = entry.getValue();

            switch (combination.getWhen()) {
                case "same_symbols" -> checkSameSymbols(matrix, sameSymbolWinningCombinations, combinationName, combination);
                case "linear_symbols" -> checkLinearSymbols(matrix, linerSymbolWinningCombinations, combinationName, combination);
            }
        }

        //merge maps
        linerSymbolWinningCombinations.forEach((key, value) -> sameSymbolWinningCombinations.merge(key, value, (existingValue, newValue) ->
                        Stream.concat(existingValue.stream(), newValue.stream())
                                .toList()
                )
        );
        return sameSymbolWinningCombinations;
    }

    private void checkLinearSymbols(String[][] matrix, Map<String, List<String>> winningCombinations, String combinationName, WinCombination combination) {
        for (var coveredArea : combination.getCoveredAreas()) {
            Map<String, Integer> symbolCount = new HashMap<>();
            for (String position : coveredArea) {
                int row = Character.getNumericValue(position.charAt(0));
                int col = Character.getNumericValue(position.charAt(2));
                String symbol = matrix[row][col];
                symbolCount.put(symbol, symbolCount.getOrDefault(symbol, 0) + 1);
            }
            for (var entry : symbolCount.entrySet()) {
                if (entry.getValue() == coveredArea.size()) {
                    winningCombinations.put(entry.getKey(), new ArrayList<>(List.of(combinationName)));
                }
            }
        }
    }

    public void checkSameSymbols(String[][] matrix, Map<String, List<String>> winningCombinations, String combinationName, WinCombination combination) {

        Map<String, Integer> symbolCount = new HashMap<>();

        for (String[] row : matrix) {
            for (String cell : row) {
                symbolCount.put(cell, symbolCount.getOrDefault(cell, 0) + 1);
            }
        }

        symbolCount.entrySet().stream()
                .filter(entry -> entry.getValue() >= combination.getCount())
                .forEach(entry -> winningCombinations.put(entry.getKey(), new ArrayList<>(List.of(combinationName))));
    }

    private double calculateReward(Map<String, List<String>> winningCombinations) {
        double totalReward = 0.0;

        for (var entry : winningCombinations.entrySet()) {
            String symbol = entry.getKey();
            List<String> combinations = entry.getValue();
            double symbolReward = betAmount * config.getSymbols().get(symbol).getRewardMultiplier();

            for (String combinationName : combinations) {
                symbolReward *= config.getWinCombinations().get(combinationName).getRewardMultiplier();
            }

            totalReward += symbolReward;
        }

        return totalReward;
    }

    public String getBonusCell(String[][] matrix) {
        Map<String, Symbol> symbols = config.getSymbols();
        for (String[] row : matrix) {
            for (String cell : row) {
                if (symbols.get(cell).getType().equals("bonus")) {
                    return cell;
                }
            }
        }
        return null;
    }

    public double applyBonusSymbol(String bonusCell, double reward) {
        if (bonusCell == null) {
            return reward;
        }

        Symbol symbol = config.getSymbols().get(bonusCell);
        switch (symbol.getImpact()) {
            case "multiply_reward" -> reward *= symbol.getRewardMultiplier();
            case "extra_bonus" -> reward += symbol.getExtra();
        }
        return reward;
    }
}
