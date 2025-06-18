package fr.kstars.forgenationsBattlepass.player;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class JsonPlayerFileLoader {
    public File loadJsonPlayerFile() throws FileNotFoundException {
        File jsonDataFile = new File("./plugins/forgenations/battlepass/players_profiles.json");

        if (!jsonDataFile.exists()) {
            throw new FileNotFoundException(jsonDataFile.getAbsolutePath());
        }

        return jsonDataFile;
    }

    public PlayerProfile[] getJsonData(File jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonFile, PlayerProfile[].class);
    }
}
