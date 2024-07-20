package net.azisaba.azisabaads.gui;

import com.github.mori01231.lifecore.util.PromptSign;
import net.azisaba.azisabaads.AzisabaAds;
import net.azisaba.azisabaads.model.Advertisement;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainScreen implements InventoryHolder {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private final Inventory inventory = Bukkit.createInventory(this, 54);
    private final Player player;
    private final List<Advertisement> advertisements = AzisabaAds.getInstance().getAdvertisements();

    public MainScreen(@NotNull Player player) {
        this.player = player;
        boolean admin = player.hasPermission("azisabaads.admin");
        int index = 0;
        for (Advertisement advertisement : advertisements) {
            if (admin || advertisement.getOwner().equals(player.getUniqueId())) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', advertisement.getMessage()));
                lore.add(ChatColor.YELLOW + "期限: " + FORMAT.format(advertisement.getExpiresAt()));
                lore.add(ChatColor.YELLOW + "プレイヤーが見た回数: " + advertisement.getImpressions());
                if (admin) {
                    lore.add("");
                    lore.add(ChatColor.RED + "Shiftクリックで削除");
                }
                lore.add("");
                lore.add(ChatColor.DARK_GRAY + "ID: " + advertisement.getId());
                inventory.setItem(index++, ItemStackUtil.createItemStack(Material.PAPER, ChatColor.YELLOW + "広告" + index, lore));
            }
        }
        if ((admin || index == 0) && advertisements.size() < 10) {
            inventory.setItem(49, ItemStackUtil.createItemStack(Material.NETHER_STAR, "§a新しい広告を作成する", Collections.emptyList()));
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public static class EventListener implements Listener {
        @EventHandler
        public void onInventoryDrag(InventoryDragEvent e) {
            if (e.getInventory().getHolder() instanceof MainScreen) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (!(e.getInventory().getHolder() instanceof MainScreen)) {
                return;
            }
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !(e.getClickedInventory().getHolder() instanceof MainScreen)) {
                return;
            }
            MainScreen screen = (MainScreen) e.getInventory().getHolder();
            if (e.getSlot() < 45 && e.getSlot() < screen.advertisements.size() && e.isShiftClick() && e.getWhoClicked().hasPermission("azisabaads.admin")) {
                Advertisement ad = screen.advertisements.get(e.getSlot());
                AzisabaAds.getInstance().getConfig().set("ads." + ad.getId(), null);
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "広告を取り下げました。");
                e.getWhoClicked().sendMessage("" + ChatColor.YELLOW + ad);
            }
            if (e.getSlot() == 49 && e.getCurrentItem() != null && !e.getCurrentItem().getType().isAir()) {
                PromptSign.promptSign(screen.player, list -> Bukkit.getScheduler().runTask(AzisabaAds.getInstance(), () -> {
                    if (list.stream().allMatch(String::isEmpty)) {
                        e.getWhoClicked().openInventory(screen.inventory);
                        return;
                    }
                    e.getWhoClicked().openInventory(new CreateAdScreen(screen.player, String.join("", list)).getInventory());
                }));
            }
        }
    }
}
