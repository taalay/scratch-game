package ae.cyberspeed.model;

import lombok.Data;

import java.util.Map;

@Data
public class Configuration {
    private int columns;
    private int rows;
    private Map<String, Symbol> symbols;
    private Probabilities probabilities;
    private Map<String, WinCombination> winCombinations;
}
