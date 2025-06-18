package fr.kstars.forgenationsBattlepass.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class JsonPlayerRepository implements PlayerRepository {
    private final Logger logger;
    private final File jsonDataFile;
    private final ArrayList<PlayerProfile> cachedPlayerProfils;

    @Override
    public void add(PlayerProfile playerProfil) {
        if (!this.cachedPlayerProfils.contains(playerProfil)) {
            this.cachedPlayerProfils.add(playerProfil);
        }
    }

    @Override
    public void removeById(UUID playerId) {
        for (PlayerProfile playerProfil : this.cachedPlayerProfils) {
            if (playerProfil.getPlayerId().equals(playerId)) {
                this.cachedPlayerProfils.remove(playerProfil);
                return;
            }
        }
    }

    @Override
    public Optional<PlayerProfile> findById(UUID playerId) {
        return this.cachedPlayerProfils.stream().filter(p -> p.getPlayerId().equals(playerId)).findFirst();
    }

    @Override
    public List<PlayerProfile> findAll() {
        return this.cachedPlayerProfils;
    }

    @Override
    public void save() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(jsonDataFile, this.cachedPlayerProfils);
        } catch (IOException e) {
            this.logger.severe(e.getMessage());
        }
    }
}
