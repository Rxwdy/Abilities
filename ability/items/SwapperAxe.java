package services.coral.ability.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.Cooldown;

import java.util.List;

public class SwapperAxe extends AbilityProvider {

    FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> cooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.Cooldown"));
    private final List<String> globalcooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.GlobalCooldown"));

    private final List<String> disabledmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ItemDisabled"));
    private final Boolean enabledmessageboolean = PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.ItemDisabledMessage");

    private final List<String> usedmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("SwapperAxe.Messages.Damager.Used"));
    private final List<String> fulldamager = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("SwapperAxe.Messages.Damager.Full"));
    private final List<String> piecenoton = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("SwapperAxe.Messages.Damager.PieceNotOn"));

    private final List<String> hitbydamaged = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("SwapperAxe.Messages.Damaged.Hit"));
    private final List<String> fulldamaged = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("SwapperAxe.Messages.Damaged.Full"));
    private final List<String> lostmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("SwapperAxe.Messages.Damaged.Lost"));

    private final int delay = PurgeAbilities.getInstance().getConfig().getInt("SwapperAxe.Settings.Delay");

    private final boolean diamondonly = PurgeAbilities.getInstance().getConfig().getBoolean("SwapperAxe.Settings.DiamondOnly.Enabled");
    private final List<String> diamondonlymessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("SwapperAxe.Settings.DiamondOnly.Enabled"));

    private final boolean fullinveffectsenabled = PurgeAbilities.getInstance().getConfig().getBoolean("SwapperAxe.Settings.FullInventoryEffects.Enabled");
    private final ConfigurationSection effects = PurgeAbilities.getInstance().getConfig().getConfigurationSection("SwapperAxe.Settings.FullInventoryEffects.Effects");

    @Override
    public String getName() {
        return "SwapperAxe";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(PurgeAbilities.getInstance().getConfig().getString("SwapperAxe.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("SwapperAxe.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return PurgeAbilities.getInstance().getConfig().getInt("SwapperAxe.Cooldown");
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.valueOf(PurgeAbilities.getInstance().getConfig().getString("SwapperAxe.Item.Material")));
    }

    @Override
    public boolean isEnabled() {
        return PurgeAbilities.getInstance().getConfig().getBoolean("SwapperAxe.Enabled");
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;
        if (!(event.getEntity() instanceof Player))
            return;
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();
        if (!isItem(damager.getItemInHand()))
            return;
        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getSwapperaxe();
        Cooldown globalCooldown = PurgeAbilities.getInstance().getCooldowns().getGlobalCooldown();
        if (globalCooldown.onCooldown(damager)) {
            globalcooldownmessage.forEach(s -> damager.sendMessage(s.replace("%time%", globalCooldown.getRemaining(damager))));
            event.setCancelled(true);
            return;
        }
        if (cooldown.onCooldown(damager)) {
            for (String s : cooldownmessage) {
                damager.sendMessage(s.replace("%time%", cooldown.getRemaining(damager)).replace("%heart%", "❤"));
            }
            event.setCancelled(true);
            return;
        }
        if (damaged.getInventory().getHelmet() == null) {
            piecenoton.forEach(s -> damager.sendMessage(s.replace("%damaged%", damaged.hasPotionEffect(PotionEffectType.INVISIBILITY)
                    ? CC.translate(PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Prefixes.InvisibilityPrefix")) : damaged.getName())));
            return;
        }
        if (diamondonly) {
            if (!(damaged.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET)) {
                diamondonlymessage.forEach(s -> damager.sendMessage(s.replace("%damaged%", damaged.hasPotionEffect(PotionEffectType.INVISIBILITY)
                        ? CC.translate(PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Prefixes.InvisibilityPrefix")) : damaged.getName())));
                return;
            }
        }
        cooldown.applyCooldown(damager, getCooldown());
        if (PurgeAbilities.getInstance().getGlobalcooldownB()) {
            globalCooldown.applyCooldown(damager, PurgeAbilities.getInstance().getGlobalcooldowntime());
            if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.GlobalCooldown.RunCommand")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.GlobalCooldown.Command"));
            }

        }
        if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.Command.Enabled")) {
                       Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.Command.Command").replace("%ability%", getDisplayName()).replace("%time%", Integer.toString(getCooldown())).replace("%player%", damager.getName()));
        }
        if (config.getBoolean(getName() + ".Sound.Enabled")) {
            damager.playSound(damager.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
        }
        usedmessage.forEach(s -> damager.sendMessage(s
                .replace("%heart%", "❤")
                .replace("%ability%", getDisplayName())
                .replace("%delay%", String.valueOf(delay))));
        hitbydamaged.forEach(s -> damaged.sendMessage(s
                .replace("%delay%", String.valueOf(delay))));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (fullinveffectsenabled) {
                    if (isFull(damaged)) {
                        giveEffects(damaged);
                        fullMessaged(damager, damaged);
                        return;
                    }
                } else {

                }
                damaged.getInventory().addItem(damaged.getInventory().getHelmet());
                damaged.getInventory().setHelmet(null);
            }
        }.runTaskLaterAsynchronously(PurgeAbilities.getInstance(), delay * 20);
    }

    public boolean isFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public void fullMessaged(Player player, Player damaged) {
        fulldamaged.forEach(damaged::sendMessage);
        fulldamager.forEach(s -> player.sendMessage(s.replace("%damaged%"
                , damaged.hasPotionEffect(PotionEffectType.INVISIBILITY)
                        ? PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Prefixes.InvisibilityPrefix") : damaged.getName())));
    }


    public void giveEffects(Player damaged) {
        for (String key : effects.getKeys(false)) {
            PotionEffect ps = new PotionEffect(PotionEffectType.getByName(PurgeAbilities.getInstance().getConfig().getString("SwapperAxe.Settings.FullInventoryEffects.Effects." + key + ".Effect"))
                    , PurgeAbilities.getInstance().getConfig().getInt("SwapperAxe.Settings.FullInventoryEffects.Effects." + key + ".Time") * 20
                    , PurgeAbilities.getInstance().getConfig().getInt("SwapperAxe.Settings.FullInventoryEffects.Effects." + key + ".Level") - 1);
            damaged.addPotionEffect(ps);
        }
    }
}
