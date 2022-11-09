package services.coral.ability.items;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;

import java.util.List;

import static org.bukkit.Sound.NOTE_PIANO;

public class LazyBrewer extends AbilityProvider {


    private final List<String> invalidmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("LazyBrewer.Messages.Invalid"));
    private final List<String> usedmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("LazyBrewer.Messages.Used"));

    @Override
    public String getName() {
        return "LazyBrewer";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(PurgeAbilities.getInstance().getConfig().getString("LazyBrewer.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("LazyBrewer.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.valueOf(PurgeAbilities.getInstance().getConfig().getString("LazyBrewer.Item.Material")));
    }

    @Override
    public boolean isEnabled() {
        return PurgeAbilities.getInstance().getConfig().getBoolean("LazyBrewer.Enabled");
    }

    @Override
    public String getInfo() {
        return "The Lazybrewer ability spawns three chests full of potions when placed.";
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        ItemStack potion = new ItemStack(Material.POTION);
        Potion splash = new Potion(1);
        splash.setType(PotionType.INSTANT_HEAL);
        splash.setLevel(2);
        splash.setSplash(true);
        splash.apply(potion);

        Player player = event.getPlayer();

        if (!isItem(player.getItemInHand())) {
            return;
        }
        if (!isEnabled()) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        Block block = event.getBlock();
        Block block2 = event.getBlock().getLocation().add(0, 1, 0).getBlock();
        Block block3 = event.getBlock().getLocation().add(0, 2, 0).getBlock();

        Block block4 = event.getBlock().getLocation().subtract(-1, 0, 0).getBlock();
        Block block5 = event.getBlock().getLocation().subtract(-1, 0, 0).add(0, 1, 0).getBlock();
        Block block6 = event.getBlock().getLocation().subtract(-1, 0, 0).add(0, 2, 0).getBlock();
        for (int x = -(1); x <= 2; x++) {
            for (int y = -(0); y <= 2; y++) {
                for (int z = -(1); z <= 1; z++) {
                    Location loc = event.getBlock().getRelative(x, y, z).getLocation();
                    if (loc.getBlock().getType() == Material.CHEST || loc.getBlock().getType() == Material.TRAPPED_CHEST) {
                        invalidmessage.forEach(player::sendMessage);
                        return;
                    }
                }
            }
        }
        block.setType(Material.CHEST);
        block2.setType(Material.CHEST);
        block3.setType(Material.CHEST);
        block4.setType(Material.CHEST);
        block5.setType(Material.CHEST);
        block6.setType(Material.CHEST);

        Chest chest = (Chest) block.getState();
        Chest chest1 = (Chest) block2.getState();
        Chest chest2 = (Chest) block3.getState();
        Chest chest3 = (Chest) block4.getState();
        Chest chest4 = (Chest) block5.getState();
        Chest chest5 = (Chest) block6.getState();

        for (int i = 0; i < 54; i++) {
            chest.getInventory().addItem(potion);
            chest1.getInventory().addItem(potion);
            chest2.getInventory().addItem(potion);
            chest3.getInventory().addItem(potion);
            chest4.getInventory().addItem(potion);
            chest5.getInventory().addItem(potion);
        }
        usedmessage.forEach(s -> player.sendMessage(s.replace("%heart%", "❤").replace("%ability%", getDisplayName())));
        if (PurgeAbilities.getInstance().getConfig().getBoolean(getName() + ".Sound.Enabled")) {
            player.playSound(player.getLocation(), Sound.valueOf(PurgeAbilities.getInstance().getConfig().getString(getName() + ".Sound.Sound")), 1f, 1f);
        }


    }

}
