package services.coral.ability.managers;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AbilityProvider implements Listener {

    public abstract String getName();

    public abstract String getDisplayName();

    public abstract List<String> getLore();

    public abstract int getCooldown();

    public abstract ItemStack getItemStack();

    public abstract boolean isEnabled();

    public abstract String getInfo();

    public abstract boolean isCustom();

    public boolean isItem(ItemStack item) {
        return item.getType().equals(getItemStack().getType())
                && item.hasItemMeta()
                && item.getItemMeta().getLore().equals(getLore())
                && item.getItemMeta().hasDisplayName();
    }

}
