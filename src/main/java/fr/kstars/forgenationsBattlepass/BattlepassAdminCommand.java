package fr.kstars.forgenationsBattlepass;

import fr.kstars.forgenationsBattlepass.player.PlayerProfile;
import fr.kstars.forgenationsBattlepass.player.PlayerRepository;
import fr.kstars.forgenationsBattlepass.reward.JsonRewardFileLoader;
import fr.kstars.forgenationsBattlepass.reward.Reward;
import fr.kstars.forgenationsBattlepass.reward.RewardRepository;
import fr.kstars.forgenationsBattlepass.util.ChatUtil;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
public class BattlepassAdminCommand implements CommandExecutor {
    public static final String RESET_PLAYER_OPTION = "resetplayer";
    public static final String SET_LEVEL_OPTION = "setlevel";
    public static final String ADD_LEVEL_OPTION = "addlevel";
    public static final String RELOAD_REWARDS_OPTION = "reloadrewards";

    private final PlayerRepository playerRepository;
    private final RewardRepository rewardRepository;
    private final JsonRewardFileLoader jsonRewardFileLoader;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String message, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /battlepass-admin <option> [arg]", NamedTextColor.RED));
            return false;
        }

        switch (args[0]) {
            case RESET_PLAYER_OPTION:
                resetPlayerOption(player, args);
                break;
            case SET_LEVEL_OPTION:
                setLevelOption(player, args);
                break;
            case ADD_LEVEL_OPTION:
                addLevelOption(player, args);
                break;
            case RELOAD_REWARDS_OPTION:
                reloadRewardsOption(player, args);
                break;
            default:
                player.sendMessage(Component.text("Usage: /battlepass-admin <option> [arg]", NamedTextColor.RED));
                return false;
        }
        return true;
    }

    private void resetPlayerOption(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(Component.text("Usage: /battlepass-admin resetplayer <player>", NamedTextColor.RED));
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[1]);

        Optional<PlayerProfile> optionalTargetProfile = this.playerRepository.findById(targetPlayer.getUniqueId());
        if (optionalTargetProfile.isEmpty()) {
            player.sendMessage(
                    Component.text("Error: Player", NamedTextColor.RED).
                            appendSpace().
                            append(Component.text(Objects.requireNonNull(targetPlayer.getName()), NamedTextColor.RED)).
                            appendSpace().
                            append(Component.text("does not exist.", NamedTextColor.RED))
            );
            return;
        }

        PlayerProfile targetProfile = optionalTargetProfile.get();

        targetProfile.setExp(0);
        player.sendMessage(ChatUtil.PLUGIN_PREFIX_WITH_COLOR
                        .append(Component.empty().decoration(TextDecoration.BOLD, false))
                        .appendSpace()
                        .append(Component.text("The", NamedTextColor.WHITE).
                                decoration(TextDecoration.BOLD, false))
                        .appendSpace()
                        .append(Component.text(Objects.requireNonNull(targetPlayer.getName()) + "'s", NamedTextColor.RED).
                                decoration(TextDecoration.BOLD, false))
                        .appendSpace()
                        .append(Component.text("profile has been", NamedTextColor.WHITE).
                                decoration(TextDecoration.BOLD, false))
                        .appendSpace()
                        .append(Component.text("reset", NamedTextColor.RED).
                                decoration(TextDecoration.BOLD, false))
                        .append(Component.text(".", NamedTextColor.WHITE).
                                decoration(TextDecoration.BOLD, false))
        );
    }

    private void setLevelOption(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage(Component.text("Usage: /battlepass-admin setlevel <level> <player>", NamedTextColor.RED));
            return;
        }

        String level = args[1];
        try {
            int levelInt = Integer.parseInt(level);
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[2]);

            Optional<PlayerProfile> optionalTargetProfile = this.playerRepository.findById(targetPlayer.getUniqueId());
            if (optionalTargetProfile.isEmpty()) {
                player.sendMessage(
                        Component.text("Error: Player", NamedTextColor.RED).
                                appendSpace().
                                append(Component.text(Objects.requireNonNull(targetPlayer.getName()), NamedTextColor.RED)).
                                appendSpace().
                                append(Component.text("does not exist.", NamedTextColor.RED))
                );
                return;
            }

            PlayerProfile targetProfile = optionalTargetProfile.get();

            int oldLevel = targetProfile.expToLevel(targetProfile.getExp());
            double newLevelExpRequired = targetProfile.levelToExp(levelInt);
            targetProfile.setExp(newLevelExpRequired);
            player.sendMessage(ChatUtil.PLUGIN_PREFIX_WITH_COLOR
                            .append(Component.empty().decoration(TextDecoration.BOLD, false))
                            .appendSpace()
                            .append(Component.text("The", NamedTextColor.WHITE).
                                    decoration(TextDecoration.BOLD, false))
                            .appendSpace()
                            .append(Component.text(Objects.requireNonNull(targetPlayer.getName()) + "'s", NamedTextColor.RED).
                                    decoration(TextDecoration.BOLD, false))
                            .appendSpace()
                            .append(Component.text("level has been", NamedTextColor.WHITE).
                                    decoration(TextDecoration.BOLD, false))
                            .appendSpace()
                            .append(Component.text("updated", NamedTextColor.RED).
                                    decoration(TextDecoration.BOLD, false))
                            .append(Component.text(".", NamedTextColor.WHITE).
                                    decoration(TextDecoration.BOLD, false))
            );

            targetProfile.checkIfRewardUnlocked((Player) targetPlayer, this.rewardRepository.findAll(), oldLevel, targetProfile.expToLevel(newLevelExpRequired));
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Usage: /battlepass-admin setlevel <level> <player>", NamedTextColor.RED));
        }
    }

    private void addLevelOption(Player player, String[] args) {
        if (args.length != 3) {
            player.sendMessage(Component.text("Usage: /battlepass-admin addlevel <level> <player>", NamedTextColor.RED));
            return;
        }

        String levelToAdd = args[1];
        try {
            int levelToAddInt = Integer.parseInt(levelToAdd);
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[2]);

            Optional<PlayerProfile> optionalTargetProfile = this.playerRepository.findById(targetPlayer.getUniqueId());
            if (optionalTargetProfile.isEmpty()) {
                player.sendMessage(
                        Component.text("Error: Player", NamedTextColor.RED).
                                appendSpace().
                                append(Component.text(Objects.requireNonNull(targetPlayer.getName()), NamedTextColor.RED)).
                                appendSpace().
                                append(Component.text("does not exist.", NamedTextColor.RED))
                );
                return;
            }

            PlayerProfile targetProfile = optionalTargetProfile.get();
            int targetLevel = targetProfile.expToLevel(targetProfile.getExp());
            double newLevelExp = targetProfile.levelToExp(targetLevel + levelToAddInt);
            targetProfile.setExp(newLevelExp);

            targetProfile.checkIfRewardUnlocked((Player) targetPlayer, this.rewardRepository.findAll(), targetLevel, targetProfile.expToLevel(newLevelExp));
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Usage: /battlepass-admin addlevel <level> <player>", NamedTextColor.RED));
        }
    }

    private void reloadRewardsOption(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(Component.text("Usage: /battlepass-admin reloadrewards", NamedTextColor.RED));
            return;
        }

        try {
            File jsonDataFile = this.jsonRewardFileLoader.loadJsonRewardFile();
            Reward[] rewardsJsonData = this.jsonRewardFileLoader.getJsonData(jsonDataFile);
            this.rewardRepository.updateRewards(rewardsJsonData);

            player.sendMessage(ChatUtil.PLUGIN_PREFIX_WITH_COLOR
                            .append(Component.empty().decoration(TextDecoration.BOLD, false))
                            .appendSpace()
                            .append(Component.text("Rewards reloaded.", NamedTextColor.WHITE)
                                    .decoration(TextDecoration.BOLD, false))
            );

        } catch (IOException e) {
            player.sendMessage(Component.text("Error: Could not load json reward file.", NamedTextColor.RED));
        }
    }
}
