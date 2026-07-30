package fr.banhammer.managers;

import fr.banhammer.BanHammerPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
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
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ItemManager(BanHammerPlugin plugin) {
        this.plugin = plugin;
        this.banKey = new NamespacedKey(plugin, "ban_hammer");
    }

    public ItemStack getBanHammer() {
        ItemStack hammer = new ItemStack(Material.MACE);
        ItemMeta meta = hammer.getItemMeta();

        if (meta != null) {
            String nameMsg = plugin.getMessage("item-name");
            if (nameMsg.isEmpty()) {
                nameMsg = "<gradient:#FF0033:#FFD700><bold>BAN HAMMER</bold></gradient>";
            }
            meta.displayName(mm.deserialize(nameMsg));

            String lang = plugin.getConfig().getString("language", "EN").toUpperCase();
            List<String> rawLore = plugin.getConfig().getStringList("messages." + lang + ".item-lore");
            if (rawLore.isEmpty()) {
                rawLore = plugin.getConfig().getStringList("messages.EN.item-lore");
            }

            List<Component> lore = new ArrayList<>();
            if (!rawLore.isEmpty()) {
                for (String line : rawLore) {
                    lore.add(mm.deserialize(line));
                }
            } else {
                lore.add(mm.deserialize("<gray>A single strike is enough...</gray>"));
                lore.add(Component.empty());
                lore.add(mm.deserialize("<dark_red><bold>Instant Ban</bold></dark_red>"));
            }
            meta.lore(lore);

            meta.setCustomModelData(plugin.getConfig().getInt("custom-model-data", 1001));

            // Apply Configured Enchantments
            ConfigurationSection enchSec = plugin.getConfig().getConfigurationSection("enchantments.list");
            boolean hideEnchants = plugin.getConfig().getBoolean("enchantments.hide-enchants", false);

            if (enchSec != null) {
                for (String key : enchSec.getKeys(false)) {
                    Enchantment ench = parseEnchantment(key);
                    if (ench != null) {
                        int level = enchSec.getInt(key, 1);
                        meta.addEnchant(ench, level, true);
                    }
                }
            } else {
                meta.addEnchant(Enchantment.UNBREAKING, 3, true);
            }

            if (hideEnchants) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setUnbreakable(true);

            meta.getPersistentDataContainer().set(banKey, PersistentDataType.BYTE, (byte) 1);

            hammer.setItemMeta(meta);
        }

        return hammer;
    }

    public Enchantment parseEnchantment(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        String cleanName = name.toLowerCase().trim();
        if (!cleanName.contains(":")) {
            cleanName = "minecraft:" + cleanName;
        }
        NamespacedKey key = NamespacedKey.fromString(cleanName);
        if (key != null) {
            Enchantment ench = Enchantment.getByKey(key);
            if (ench != null) return ench;
        }
        return Enchantment.getByName(name.toUpperCase());
    }

    public boolean isBanHammer(ItemStack item) {
        if (item == null || item.getType() != Material.MACE) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        // 1. Check PersistentDataContainer
        if (meta.getPersistentDataContainer().has(banKey, PersistentDataType.BYTE)) {
            return true;
        }

        // 2. Check CustomModelData fallback
        if (meta.hasCustomModelData() && meta.getCustomModelData() == plugin.getConfig().getInt("custom-model-data", 1001)) {
            return true;
        }

        // 3. Check DisplayName fallback
        if (meta.hasDisplayName()) {
            String plainName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
            return plainName.toUpperCase().contains("BAN HAMMER");
        }

        return false;
    }
}
