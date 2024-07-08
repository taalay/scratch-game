package ae.cyberspeed;

import ae.cyberspeed.model.Configuration;
import ae.cyberspeed.model.Result;
import ae.cyberspeed.service.ScratchGame;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws IOException {

        String configFilePath = null;
        double bettingAmount = 0.0;

        for (int i = 0; i < args.length; i++) {
            if ("--config".equals(args[i]) && i + 1 < args.length) {
                configFilePath = args[i + 1];
            } else if ("--betting-amount".equals(args[i]) && i + 1 < args.length) {
                bettingAmount = Double.parseDouble(args[i + 1]);
            }
        }

        if (configFilePath == null || bettingAmount == 0.0) {
            System.out.println("Usage: java -jar scratch-game.jar --config <config_file_path> --betting-amount <betting-amount>");
            return;
        }


        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        Configuration config = mapper.readValue(new File(configFilePath), Configuration.class);

        ScratchGame game = new ScratchGame(config, bettingAmount, new Random());
        Result result = game.play();

        System.out.println(result);
    }
}