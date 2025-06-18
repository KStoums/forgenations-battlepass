package fr.kstars.forgenationsBattlepass;

import fr.kstars.forgenationsBattlepass.player.PlayerProfile;
import fr.kstars.forgenationsBattlepass.player.PlayerRepository;
import fr.kstars.forgenationsBattlepass.reward.Reward;
import fr.kstars.forgenationsBattlepass.reward.RewardRepository;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

@AllArgsConstructor
public class BattlepassInventory {
    private final RewardRepository rewardRepository;
    private final PlayerRepository playerRepository;
    public static final Component NEXT_PAGE_ITEM_NAME = Component.text("Page suivante", NamedTextColor.GRAY);
    public static final Component PREVIOUS_PAGE_ITEM_NAME = Component.text("Page précédente", NamedTextColor.GRAY);
    public static final Component PROFILE_ITEM_NAME = Component.text("●", NamedTextColor.BLUE, TextDecoration.BOLD).
            appendSpace().
            append(Component.text("Profile", NamedTextColor.BLUE, TextDecoration.BOLD));
    public static final String BATTLEPASS_INVENTORY_NAME = "§9§lBattlepass §7| §9Niveau §l%levelStart% §9à §l%levelEnd%"; //Change the name if you wish, but do not remove "%levelStart% à %levelEnd%".

    public static final int INVENTORY_SLOT_SIZE = 9;

    public Optional<Inventory> createInventory(int page, UUID playerUuid) {
        Optional<PlayerProfile> playerProfile = this.playerRepository.findById(playerUuid);
        if (playerProfile.isEmpty()) {
            return Optional.empty();
        }

        int pageEndLevel = INVENTORY_SLOT_SIZE * page;
        int pageStartLevel = (pageEndLevel - INVENTORY_SLOT_SIZE) + 1; //+1 because the first level is not 0 but 1

        String InventoryName = BATTLEPASS_INVENTORY_NAME.replace("%levelStart%", String.valueOf(pageStartLevel)).
                replace("%levelEnd%", String.valueOf(pageEndLevel));

        Inventory inventory = Bukkit.createInventory(null, 45, Component.text(InventoryName));
        addFreeItems(inventory, page, playerProfile.get());
        addProgressItems(inventory, page, playerProfile.get());
        addNavigationAndProfileItems(inventory, playerProfile.get(), page);
        addPremiumItems(inventory, page, playerProfile.get());
        return Optional.of(inventory);
    }

    private void addFreeItems(Inventory inventory, int page, PlayerProfile playerProfile) {
        List<Reward> rewards = this.rewardRepository.findByPage(page, false);

        for (Reward reward : rewards) {
            ItemStack item = new ItemStack(Material.HOPPER_MINECART);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(reward.getName()));

            List<Component> lore = new ArrayList<>();
            reward.getDescription().forEach(desc -> lore.add(Component.text(desc)));
            meta.lore(lore);

            if (playerProfile.expToLevel(playerProfile.getExp()) >= reward.getLevel()) {
                meta.addEnchant(Enchantment.LOYALTY, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);

            inventory.setItem(this.getItemSlot(reward.getLevel(), false), item);
        }
    }

    private void addPremiumItems(Inventory inventory, int page, PlayerProfile playerProfile) {
        List<Reward> rewards = this.rewardRepository.findByPage(page, true);

        for (Reward reward : rewards) {
            ItemStack item = new ItemStack(Material.CHEST_MINECART);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(reward.getName()));

            List<Component> lore = new ArrayList<>();
            reward.getDescription().forEach(desc -> lore.add(Component.text(desc)));
            meta.lore(lore);

            if (playerProfile.expToLevel(playerProfile.getExp()) >= reward.getLevel()) {
                meta.addEnchant(Enchantment.LOYALTY, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);

            inventory.setItem(this.getItemSlot(reward.getLevel(), true), item);
        }
    }

    private void addProgressItems(Inventory inventory, int page, PlayerProfile playerProfile) {
        int pageLevelIndex = (INVENTORY_SLOT_SIZE * page) - 8;

        for (int i = 9; i < 18; i++) { //9 = First inventory slot where to start progress items & 19 = last inventory slots
            if (playerProfile.expToLevel(playerProfile.getExp()) >= pageLevelIndex) {
                ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
                ItemMeta meta = item.getItemMeta();
                meta.displayName(Component.text("DÉBLOQUÉ", NamedTextColor.DARK_GREEN));
                item.setItemMeta(meta);
                inventory.setItem(i, item);

                pageLevelIndex++;
                continue;
            }

            ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text("BLOQUÉ", NamedTextColor.RED));
            item.setItemMeta(meta);
            inventory.setItem(i, item);
            pageLevelIndex++;
        }
    }

    private void addNavigationAndProfileItems(Inventory inventory, PlayerProfile playerProfile, int page) {
        ItemStack navigationItem = new ItemStack(Material.ARROW);
        ItemMeta meta = navigationItem.getItemMeta();
        if (page > 1) {
            meta.displayName(PREVIOUS_PAGE_ITEM_NAME);
            navigationItem.setItemMeta(meta);
            inventory.setItem(39, navigationItem);
        }

        if (!this.rewardRepository.findByPage(page+1, false).isEmpty() || !this.rewardRepository.findByPage(page+1, true).isEmpty()) {
            meta.displayName(NEXT_PAGE_ITEM_NAME);
            navigationItem.setItemMeta(meta);
            inventory.setItem(41, navigationItem);
        }

        ItemStack playerProfileItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta playerProfileSkullMeta = (SkullMeta) playerProfileItem.getItemMeta();

        String playerUsername = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(Bukkit.getPlayer(playerProfile.getPlayerId())).displayName());
        playerProfileSkullMeta.displayName(PROFILE_ITEM_NAME);
        playerProfileSkullMeta.lore(List.of(
                Component.text("Nom:", NamedTextColor.WHITE).
                        appendSpace().
                        append(Component.text(playerUsername, NamedTextColor.GRAY)),
                Component.text("Niveau:", NamedTextColor.WHITE).
                        appendSpace().
                        append(Component.text(playerProfile.expToLevel(playerProfile.getExp()), NamedTextColor.GRAY)),
                Component.text("Expérience:", NamedTextColor.WHITE).
                        appendSpace().
                        append(Component.text((int) playerProfile.getExp(), NamedTextColor.GRAY))
        ));
        playerProfileSkullMeta.setOwningPlayer(Bukkit.getPlayer(playerProfile.getPlayerId()));
        playerProfileItem.setItemMeta(playerProfileSkullMeta);
        inventory.setItem(40, playerProfileItem);
    }

    private int getItemSlot(int itemLevel, boolean premium) {
        if (!premium) {
            return (itemLevel - 1) % INVENTORY_SLOT_SIZE;
        }

        return 18 + (itemLevel - 1) % INVENTORY_SLOT_SIZE;
    }
}
