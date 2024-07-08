package ae.cyberspeed.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Result {

    private double betAmount;
    private String[][] matrix;
    private double reward;
    private Map<String, List<String>> appliedWinningCombinations;
    private String appliedBonusSymbol;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bet Amount: ").append(betAmount).append("\n");
        sb.append("Matrix:\n");
        for (String[] row : matrix) {
            for (String cell : row) {
                sb.append(cell).append(" ");
            }
            sb.append("\n");
        }
        sb.append("Reward: ").append(reward).append("\n");
        sb.append("Applied Winning Combinations: ").append(appliedWinningCombinations).append("\n");
        sb.append("Applied Bonus Symbol: ").append(appliedBonusSymbol).append("\n");
        return sb.toString();
    }
}