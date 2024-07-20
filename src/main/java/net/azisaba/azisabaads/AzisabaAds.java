package net.azisaba.azisabaads;

import net.azisaba.azisabaads.commands.AdsCommand;
import net.azisaba.azisabaads.gui.CreateAdScreen;
import net.azisaba.azisabaads.gui.MainScreen;
import net.azisaba.azisabaads.model.Advertisement;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class AzisabaAds extends JavaPlugin {
    public static @NotNull AzisabaAds getInstance() {
        return AzisabaAds.getPlugin(AzisabaAds.class);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new MainScreen.EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new CreateAdScreen.EventListener(), this);
        Objects.requireNonNull(getCommand("ads")).setExecutor(new AdsCommand());
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::broadcast, 20 * 60 * 15, 20 * 60 * 15); // every 15 minutes
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public @NotNull Economy getEconomy() {
        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider == null) {
            throw new RuntimeException("Economy is not loaded");
        }
        return Objects.requireNonNull(provider.getProvider(), "provided economy was null");
    }

    public void broadcast() {
        List<Advertisement> list = getAdvertisements();
        if (list.isEmpty()) {
            return;
        }
        Collections.shuffle(list);
        Advertisement ad = list.get(0);
        Bukkit.broadcastMessage("§e§l[広告] §f" + ChatColor.translateAlternateColorCodes('&', ad.getMessage()) + " §8(" + Bukkit.getOfflinePlayer(ad.getOwner()).getName() + ")");
        int impressions = ad.getImpressions() + Bukkit.getOnlinePlayers().size();
        getConfig().set("ads." + ad.getId() + ".impressions", impressions);
    }

    public @NotNull List<@NotNull Advertisement> getAdvertisements() {
        ConfigurationSection section = getConfig().getConfigurationSection("ads");
        if (section == null) {
            return Collections.emptyList();
        }
        return section.getKeys(false).stream().map(id -> {
            UUID owner = UUID.fromString(Objects.requireNonNull(section.getString(id + ".owner"), "owner"));
            String message = Objects.requireNonNull(section.getString(id + ".message"), "message");
            long expiresAt = section.getLong(id + ".expiresAt");
            int impressions = section.getInt(id + ".impressions", 0);
            return new Advertisement(UUID.fromString(id), owner, message, expiresAt, impressions);
        }).filter(ad -> ad.getExpiresAt() > System.currentTimeMillis())
                .sorted(Comparator.comparing(Advertisement::getId))
                .collect(Collectors.toList());
    }

    public void ban(@NotNull UUID uuid, long expiresAt) {
        getConfig().set("banned." + uuid, expiresAt);
        saveConfig();
    }

    public boolean isBanned(@NotNull UUID uuid) {
        return getConfig().getLong("banned." + uuid) > System.currentTimeMillis();
    }
}
