package net.azisaba.azisabaads.gui;

import net.azisaba.azisabaads.AzisabaAds;
import net.azisaba.azisabaads.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CreateAdScreen implements InventoryHolder {
    private final Inventory inventory = Bukkit.createInventory(this, 54);
    private final Player player;
    private final String message;
    private AdDuration duration = AdDuration.ONE_DAY;

    public CreateAdScreen(@NotNull Player player, @NotNull String message) {
        this.player = player;
        this.message = message;
        resetItems();
    }

    public void resetItems() {
        inventory.setItem(11, ItemStackUtil.createItemStack(
                Material.PAPER,
                "§eメッセージ",
                Arrays.asList(
                        "§f" + ChatColor.translateAlternateColorCodes('&', message),
                        "",
                        "§7変更するには前の画面に戻ってください"
                )
        ));
        inventory.setItem(13, ItemStackUtil.createItemStack(Material.PLAYER_HEAD, "§e広告主", Collections.singletonList("§f" + player.getName())));
        List<String> durationLore = Arrays.stream(AdDuration.values()).map(duration -> {
            if (this.duration == duration) {
                return "§f - §a" + duration.label;
            } else {
                return "§f - " + duration.label;
            }
        }).collect(Collectors.toList());
        inventory.setItem(15, ItemStackUtil.createItemStack(Material.POWERED_RAIL, "§e期間", durationLore));
        inventory.setItem(31, ItemStackUtil.createItemStack(Material.NETHER_STAR, "§a広告を出稿する", Collections.singletonList("§b" + calculateCost() + "円§e必要です！")));
        inventory.setItem(49, ItemStackUtil.createItemStack(Material.ARROW, "§e戻る", Collections.emptyList()));
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public long calculateCost() {
        long cost = duration.cost;
        if (message.length() > 20) {
            cost *= 2;
        }
        return cost;
    }

    public enum AdDuration {
        ONE_DAY("1日", 800_000, 1000L * 60 * 60 * 24),
        TWO_DAYS("2日", 1_500_000, 1000L * 60 * 60 * 24 * 2),
        THREE_DAYS("3日", 2_200_000, 1000L * 60 * 60 * 24 * 3),
        FOUR_DAYS("4日", 2_900_000, 1000L * 60 * 60 * 24 * 4),
        FIVE_DAYS("5日", 3_600_000, 1000L * 60 * 60 * 24 * 5),
        SIX_DAYS("6日", 4_300_000, 1000L * 60 * 60 * 24 * 6),
        ONE_WEEK("1週間", 5_000_000, 1000L * 60 * 60 * 24 * 7),
        TWO_WEEKS("2週間", 10_000_000, 1000L * 60 * 60 * 24 * 14),
        THREE_WEEKS("3週間", 15_000_000, 1000L * 60 * 60 * 24 * 21),
        ;

        public final String label;
        public final long cost;
        public final long milliseconds;

        AdDuration(String label, long cost, long milliseconds) {
            this.label = label;
            this.cost = cost;
            this.milliseconds = milliseconds;
        }
    }

    public static class EventListener implements Listener {
        @EventHandler
        public void onInventoryDrag(InventoryDragEvent e) {
            if (e.getInventory().getHolder() instanceof CreateAdScreen) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (!(e.getInventory().getHolder() instanceof CreateAdScreen)) {
                return;
            }
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !(e.getClickedInventory().getHolder() instanceof CreateAdScreen)) {
                return;
            }
            CreateAdScreen screen = (CreateAdScreen) e.getInventory().getHolder();
            if (e.getSlot() == 15) {
                int newIndex = (screen.duration.ordinal() + 1) % AdDuration.values().length;
                screen.duration = AdDuration.values()[newIndex];
                screen.resetItems();
            }
            if (e.getSlot() == 31) {
                long cost = screen.calculateCost();
                if (AzisabaAds.getInstance().getEconomy().withdrawPlayer(screen.player, cost).transactionSuccess()) {
                    UUID id = UUID.randomUUID();
                    AzisabaAds.getInstance().getConfig().set("ads." + id + ".owner", screen.player.getUniqueId().toString());
                    AzisabaAds.getInstance().getConfig().set("ads." + id + ".message", screen.message);
                    AzisabaAds.getInstance().getConfig().set("ads." + id + ".expiresAt", System.currentTimeMillis() + screen.duration.milliseconds);
                    AzisabaAds.getInstance().saveConfig();
                    e.getWhoClicked().openInventory(new MainScreen(screen.player).getInventory());
                } else {
                    e.getWhoClicked().sendMessage(ChatColor.RED + "お金が足りません。");
                    e.getWhoClicked().closeInventory();
                }
            }
            if (e.getSlot() == 49) {
                e.getWhoClicked().openInventory(new MainScreen(screen.player).getInventory());
            }
        }
    }
}
