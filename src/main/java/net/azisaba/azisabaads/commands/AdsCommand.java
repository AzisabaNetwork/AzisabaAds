package net.azisaba.azisabaads.commands;

import net.azisaba.azisabaads.AzisabaAds;
import net.azisaba.azisabaads.gui.MainScreen;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AdsCommand implements TabExecutor {
    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender.hasPermission("azisabaads.admin")) {
            if (args.length == 1 && args[0].equals("debug")) {
                AzisabaAds.getInstance().broadcast();
                return true;
            }
            if (args.length == 3 && args[0].equals("ban")) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                if (player.getUniqueId().version() != 4) {
                    sender.sendMessage(ChatColor.RED + "そんなプレイヤーは存在しないようです");
                    return true;
                }
                long day = 1000L * 60 * 60 * 24;
                long input = Long.parseLong(args[2]);
                long expiresAt = System.currentTimeMillis() + day * input;
                AzisabaAds.getInstance().ban(player.getUniqueId(), expiresAt);
                sender.sendMessage(ChatColor.GREEN + player.getName() + "を" + input + "日間、広告出稿停止にしました");
                return true;
            }
        }
        if (AzisabaAds.getInstance().isBanned(((Player) sender).getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "この機能は現在利用できません。");
            return true;
        }
        ((Player) sender).openInventory(new MainScreen((Player) sender).getInventory());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Collections.emptyList();
    }
}
