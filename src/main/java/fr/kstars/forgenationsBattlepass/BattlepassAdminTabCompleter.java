package fr.kstars.forgenationsBattlepass;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BattlepassAdminTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            return List.of();
        }

        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            List<String> options = List.of(
                    BattlepassAdminCommand.RESET_PLAYER_OPTION,
                    BattlepassAdminCommand.SET_LEVEL_OPTION,
                    BattlepassAdminCommand.ADD_LEVEL_OPTION,
                    BattlepassAdminCommand.RELOAD_REWARDS_OPTION
            );

            for (String option : options) {
                if (option.startsWith(args[0].toLowerCase())) {
                    suggestions.add(option);
                }
            }
            return suggestions;
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case BattlepassAdminCommand.ADD_LEVEL_OPTION, BattlepassAdminCommand.SET_LEVEL_OPTION:
                    return List.of("1", "5", "10", "20", "50");
                case  BattlepassAdminCommand.RESET_PLAYER_OPTION:
                    List<String> players = new ArrayList<>();
                    Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
                    return players;
            }
        }

        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case BattlepassAdminCommand.SET_LEVEL_OPTION, BattlepassAdminCommand.ADD_LEVEL_OPTION:
                    List<String> players = new ArrayList<>();
                    Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
                    return players;
            }
        }

        return List.of();
    }
}

