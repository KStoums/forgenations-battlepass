package fr.kstars.forgenationsBattlepass;

import fr.kstars.forgenationsBattlepass.player.PlayerRepository;
import fr.kstars.forgenationsBattlepass.reward.RewardRepository;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class InventoryClickListener implements Listener {
    private final RewardRepository rewardRepository;
    private final PlayerRepository playerRepository;

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }

        String inventoryTitle = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (inventoryTitle.isEmpty()) {
            return;
        }

        Optional<Integer> currentPage = getCurrentPage(inventoryTitle);
        if (currentPage.isEmpty()) {
            return;
        }

        Player playerWhoClicked = Bukkit.getPlayer(event.getWhoClicked().getUniqueId());
        if (playerWhoClicked == null) {
            return;
        }

        String itemDisplayName = PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().displayName());
        String nextPageItemNameString = PlainTextComponentSerializer.plainText().serialize(BattlepassInventory.NEXT_PAGE_ITEM_NAME);
        String previousPageItemNameString = PlainTextComponentSerializer.plainText().serialize(BattlepassInventory.PREVIOUS_PAGE_ITEM_NAME);

        if (itemDisplayName.equals("[" + nextPageItemNameString + "]")) {
            Inventory nextPageBattlepassInventory = new BattlepassInventory(this.rewardRepository, playerRepository).createInventory(currentPage.get()+1, playerWhoClicked.getUniqueId());
            playerWhoClicked.openInventory(nextPageBattlepassInventory);
            playerWhoClicked.playSound(playerWhoClicked.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);

        } else if (itemDisplayName.equals("[" + previousPageItemNameString + "]")) {
            Inventory previousPageBattlepassInventory = new BattlepassInventory(this.rewardRepository, playerRepository).createInventory(currentPage.get()-1, playerWhoClicked.getUniqueId());
            playerWhoClicked.openInventory(previousPageBattlepassInventory);
            playerWhoClicked.playSound(playerWhoClicked.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
        }

        event.setCancelled(true);
    }

    private Optional<Integer> getCurrentPage(String inventoryTitle) {
        Pattern pattern = Pattern.compile("à §l(\\d+)");
        Matcher matcher = pattern.matcher(inventoryTitle);

        if (matcher.find()) {
            return Optional.of((int) Math.ceil(Integer.parseInt(matcher.group(1)) / 9.0));
        }

        return Optional.empty();
    }
}
