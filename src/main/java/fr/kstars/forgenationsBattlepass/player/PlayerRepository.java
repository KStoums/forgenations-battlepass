package fr.kstars.forgenationsBattlepass.player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {
    void add(PlayerProfile playerProfil);

    void removeById(UUID playerId);

    Optional<PlayerProfile> findById(UUID playerId);

    List<PlayerProfile> findAll();

    void save();
}