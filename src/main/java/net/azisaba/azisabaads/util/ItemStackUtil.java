package net.azisaba.azisabaads.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ItemStackUtil {
    public static @NotNull ItemStack createItemStack(@NotNull Material material, @NotNull String name, @NotNull List<String> lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = Objects.requireNonNull(stack.getItemMeta());
        meta.setDisplayName(name);
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }
        stack.setItemMeta(meta);
        return stack;
    }
}
