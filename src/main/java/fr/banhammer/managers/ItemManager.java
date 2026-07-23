package fr.banhammer.managers;

import fr.banhammer.BanHammerPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ItemManager(BanHammerPlugin plugin) {
        this.plugin = plugin;
        this.banKey = new NamespacedKey(plugin, "ban_hammer");
    }

    public ItemStack getBanHammer() {
        ItemStack hammer = new ItemStack(Material.MACE);
        ItemMeta meta = hammer.getItemMeta();

        if (meta != null) {
            meta.displayName(mm.deserialize("<gradient:#FF0033:#FFD700><bold>BAN HAMMER</bold></gradient>"));

            List<Component> lore = new ArrayList<>();
            lore.add(mm.deserialize("<gray>Un seul coup suffit...</gray>"));
            lore.add(Component.empty());
            lore.add(mm.deserialize("<dark_red><bold>Bannissement immédiat</bold></dark_red>"));
            meta.lore(lore);

            meta.setCustomModelData(plugin.getConfig().getInt("custom-model-data", 1001));
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setUnbreakable(true);

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
