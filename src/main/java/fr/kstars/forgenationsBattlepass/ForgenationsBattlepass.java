package fr.kstars.forgenationsBattlepass;

import fr.kstars.forgenationsBattlepass.player.*;
import fr.kstars.forgenationsBattlepass.reward.JsonRewardFileLoader;
import fr.kstars.forgenationsBattlepass.reward.JsonRewardRepository;
import fr.kstars.forgenationsBattlepass.reward.Reward;
import fr.kstars.forgenationsBattlepass.util.ChatUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ForgenationsBattlepass extends JavaPlugin {
    private PlayerRepository playerRepository;

    @Override
    public void onEnable() {
        getLogger().info(ChatUtil.PLUGIN_PREFIX_WITHOUT_COLOR + "Initializing plugin...");
        try {
            //Player Repository
            JsonPlayerFileLoader jsonPlayerFileLoader = new JsonPlayerFileLoader();
            File jsonPlayerFile = jsonPlayerFileLoader.loadJsonPlayerFile();
            PlayerProfile[] jsonPlayerData = jsonPlayerFileLoader.getJsonData(jsonPlayerFile);
            JsonPlayerRepository playerRepository = new JsonPlayerRepository(getLogger(), jsonPlayerFile, new ArrayList<>(Arrays.asList(jsonPlayerData)));
            this.playerRepository = playerRepository;

            //Reward Repository
            JsonRewardFileLoader jsonRewardFileLoader = new JsonRewardFileLoader();
            File jsonRewardFile = jsonRewardFileLoader.loadJsonRewardFile();
            Reward[] jsonRewardData = jsonRewardFileLoader.getJsonData(jsonRewardFile);
            JsonRewardRepository rewardRepository = new JsonRewardRepository(List.of(jsonRewardData));

            //Commands
            Objects.requireNonNull(getCommand("battlepass")).setExecutor(new BattlepassCommand(rewardRepository, playerRepository));
            Objects.requireNonNull(getCommand("battlepass-admin")).setExecutor(new BattlepassAdminCommand(playerRepository, rewardRepository, jsonRewardFileLoader));

            //Tab Completer
            Objects.requireNonNull(getCommand("battlepass-admin")).setTabCompleter(new BattlepassAdminTabCompleter());

            //Events
            getServer().getPluginManager().registerEvents(new InventoryClickListener(rewardRepository, playerRepository), this);
            getServer().getPluginManager().registerEvents(new EntityDeathListener(playerRepository, rewardRepository), this);

            //Tasks
            getServer().getScheduler().runTaskTimerAsynchronously(this, new PlayerSaveDataTask(playerRepository), 0, 1200 * 5);
        } catch (Exception e) {
            getLogger().severe(e.getMessage());
        }

        getLogger().info(ChatUtil.PLUGIN_PREFIX_WITHOUT_COLOR + "Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        if (this.playerRepository != null) {
            playerRepository.save();
        }

        getLogger().info(ChatUtil.PLUGIN_PREFIX_WITHOUT_COLOR + "Plugin Disabled!");
    }
}
