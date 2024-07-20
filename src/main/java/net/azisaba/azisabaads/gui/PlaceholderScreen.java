package net.azisaba.azisabaads.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class PlaceholderScreen implements InventoryHolder {
    private final Inventory inventory = Bukkit.createInventory(this, 54);
    private final Player player;

    public PlaceholderScreen(@NotNull Player player) {
        this.player = player;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public static class EventListener implements Listener {
        @EventHandler
        public void onInventoryDrag(InventoryDragEvent e) {
            if (e.getInventory().getHolder() instanceof PlaceholderScreen) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (!(e.getInventory().getHolder() instanceof PlaceholderScreen)) {
                return;
            }
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !(e.getClickedInventory().getHolder() instanceof PlaceholderScreen)) {
                return;
            }
            PlaceholderScreen screen = (PlaceholderScreen) e.getInventory().getHolder();
        }
    }
}
