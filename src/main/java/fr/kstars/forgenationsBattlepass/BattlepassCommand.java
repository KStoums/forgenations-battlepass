package fr.kstars.forgenationsBattlepass;

import fr.kstars.forgenationsBattlepass.player.PlayerRepository;
import fr.kstars.forgenationsBattlepass.reward.RewardRepository;
import fr.kstars.forgenationsBattlepass.util.ChatUtil;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@AllArgsConstructor
public class BattlepassCommand implements CommandExecutor {
    private final RewardRepository rewardRepository;
    private final PlayerRepository playerRepository;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String message, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        Optional<Inventory> battlepassInventory = new BattlepassInventory(this.rewardRepository, this.playerRepository).createInventory(1, player.getUniqueId());
        if (battlepassInventory.isEmpty()) {
            player.sendMessage(ChatUtil.ERR_PREFIX.
                    append(Component.text("Une erreur est survenue, veuillez contacter un Maître du Jeu.")));
        }

        player.openInventory(battlepassInventory.get());
        return true;
    }
}
