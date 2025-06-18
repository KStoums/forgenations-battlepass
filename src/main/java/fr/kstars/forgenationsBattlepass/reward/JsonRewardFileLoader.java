package fr.kstars.forgenationsBattlepass.reward;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonRewardFileLoader {
    public File loadJsonRewardFile() throws IOException {
        File jsonDataFile = new File("./plugins/forgenations-battlepass/data/rewards.json");
        File parentFile = jsonDataFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        if (!jsonDataFile.exists()) {
            ObjectMapper mapper = new ObjectMapper();

            Reward[] exempleRewards = new Reward[] {
                    new Reward("§cExample", List.of("§cExample"), "give %player% minecraft:diamond 1", 1, false)
            };

            mapper.writeValue(jsonDataFile, exempleRewards);
        }

        return jsonDataFile;
    }

    public Reward[] getJsonData(File jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonFile, Reward[].class);
    }
}
