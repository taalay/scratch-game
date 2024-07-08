package ae.cyberspeed.model;

import lombok.Data;

import java.util.Map;

@Data
public class StandardSymbolProbability {
    private int column;
    private int row;
    private Map<String, Integer> symbols;
}
