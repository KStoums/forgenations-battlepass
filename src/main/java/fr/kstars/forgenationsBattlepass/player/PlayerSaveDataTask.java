package fr.kstars.forgenationsBattlepass.player;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerSaveDataTask implements Runnable {
    private final PlayerRepository repository;

    @Override
    public void run() {
        try {
            this.repository.save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}