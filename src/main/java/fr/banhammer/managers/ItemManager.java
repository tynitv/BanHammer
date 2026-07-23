package fr.banhammer.managers;

import fr.banhammer.BanHammerPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    private final BanHammerPlugin plugin;
    private final NamespacedKey banKey;

    public ItemManager(BanHammerPlugin plugin) {
        this.plugin = plugin;
        this.banKey = new NamespacedKey(plugin, "ban_hammer");
    }

    public ItemStack getBanHammer() {
        ItemStack hammer = new ItemStack(Material.MACE);
        ItemMeta meta = hammer.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("§c§lBAN HAMMER"));

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§7Un seul coup suffit..."));
            lore.add(Component.text(""));
            lore.add(Component.text("§4Bannissement immédiat"));
            meta.lore(lore);

            // Glint effect
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); // Hide attack damage etc for cleaner look

            meta.setUnbreakable(true);

            // Custom Tag
            meta.getPersistentDataContainer().set(banKey, PersistentDataType.BYTE, (byte) 1);

            hammer.setItemMeta(meta);
        }

        return hammer;
    }

    public boolean isBanHammer(ItemStack item) {
        if (item == null || item.getType() != Material.MACE) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(banKey, PersistentDataType.BYTE);
    }
}
