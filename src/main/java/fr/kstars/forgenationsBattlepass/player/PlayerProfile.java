package fr.kstars.forgenationsBattlepass.player;

import fr.kstars.forgenationsBattlepass.reward.Reward;
import fr.kstars.forgenationsBattlepass.util.ChatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor // <- Required for Jackson
@Setter
@Getter
public class PlayerProfile {
    private static double XP_BASE = 100;
    private static double GROWTH_RATE = 1.5;

    private UUID playerId;
    private double exp;

    public void checkIfRewardUnlocked(Player player, List<Reward> rewards, int oldLevel, int newLevel) {
        List<String> rewardsName = new ArrayList<>();
        for (Reward reward : rewards) {
            if (reward.getLevel() <= oldLevel || reward.getLevel() > newLevel) {
                continue;
            }

            if (reward.isPremium() && player.hasPermission("battlepass.premium")) {
                continue;
            }

            if (reward.getCommand().isEmpty()) {
                continue;
            }

            if (!reward.getCommand().contains("%player%")) {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), reward.getCommand());
            } else {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), reward.getCommand().replace("%player%", player.getName()));
            }

            rewardsName.add(reward.getName());
        }

        player.sendMessage(ChatUtil.PLUGIN_PREFIX_WITH_COLOR.
                append(Component.empty().decoration(TextDecoration.BOLD, false)).
                appendSpace().
                append(Component.text("Vous êtes monté d'un niveau ! Vous avez débloqué", NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false)).
                appendSpace().
                append(Component.text(String.join(", ", rewardsName), NamedTextColor.RED).decoration(TextDecoration.BOLD, false)).
                append(Component.text(".", NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false))
        );
    }

    public boolean addExpCheckLevelUp(double exp) {
        int oldLevel = expToLevel(this.exp);
        this.exp = this.exp + exp;
        int newLevel = expToLevel(this.exp);

        return newLevel > oldLevel;
    }

    public int expToLevel(double exp) {
        double total = 0;
        int level = 0;

        while (true) {
            double required = XP_BASE * Math.pow(GROWTH_RATE, level - 1);
            if (total + required > exp) break;

            total += required;
            level++;
        }

        return level;
    }

    public double levelToExp(int level) {
        double total = 0;

        for (int i = 0; i < level; i++) {
            total += XP_BASE * Math.pow(GROWTH_RATE, i - 1);
        }

        return total;
    }
}