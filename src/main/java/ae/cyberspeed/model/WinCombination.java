package ae.cyberspeed.model;

import lombok.Data;

import java.util.List;

@Data
public class WinCombination {
    private double rewardMultiplier;
    private String when;
    private Integer count;
    private String group;
    private List<List<String>> coveredAreas;
}
