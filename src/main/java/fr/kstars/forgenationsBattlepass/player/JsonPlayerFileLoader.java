package fr.kstars.forgenationsBattlepass.player;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonPlayerFileLoader {
    public File loadJsonPlayerFile() throws IOException {
        File jsonDataFile = new File("./plugins/forgenations/battlepass/players_profiles.json");

        if (!jsonDataFile.exists()) {
            ObjectMapper mapper = new ObjectMapper();

            PlayerProfile[] exampleProfiles = new PlayerProfile[] {};
            mapper.writeValue(jsonDataFile, exampleProfiles);
        }

        return jsonDataFile;
    }

    public PlayerProfile[] getJsonData(File jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonFile, PlayerProfile[].class);
    }
}
