package services.coral.ability.items;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;

import java.util.List;

public class SmelterShovel extends AbilityProvider {
    private final FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> disabledmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ItemDisabled"));
    private final Boolean enabledmessageboolean = config.getBoolean("PurgeAbilities.Settings.ItemDisabledMessage");

    @Override
    public String getName() {
        return "SmelterShovel";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(config.getString("SmelterShovel.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(config.getStringList("SmelterShovel.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.valueOf(config.getString("SmelterShovel.Item.Material")));
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("SmelterShovel.Enabled");
    }

    @Override
    public String getInfo() {
        return "The SmelterShovel ability item is an item, when used to break glass, instead of dropping a sand block on the ground, it will automatically give 1 piece of glass to your inventory";
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Material material = event.getBlock().getType();
        Player player = event.getPlayer();
        if (!isItem(player.getItemInHand())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        if (!isEnabled()) {
            if (enabledmessageboolean) {
                for (String s : disabledmessage) {
                    player.sendMessage(s.replace("%ability%", getDisplayName()).replace("%heart%", "❤"));
                }
            }
            return;
        }

        if (material.equals(Material.SAND)) {
            event.getBlock().setType(Material.AIR);
            ItemStack itemstack = new ItemStack(Material.GLASS);
            player.getInventory().addItem(itemstack);
            if (config.getBoolean(getName() + ".Sound.Enabled")) {
                player.playSound(player.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
            }

        }


    }
}
