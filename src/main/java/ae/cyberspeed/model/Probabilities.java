package ae.cyberspeed.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Probabilities {
    private List<StandardSymbolProbability> standardSymbols;

    private BonusSymbols bonusSymbols;

    public Map<String, Integer> getBonusSymbols() {
        return bonusSymbols.getSymbols();
    }

    @Data
    public static class BonusSymbols {
        private Map<String, Integer> symbols;
    }
}