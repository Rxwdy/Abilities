package services.coral.ability.items;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.Cooldown;

import java.util.List;

public class Snowport extends AbilityProvider {
    //comment
    private final FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> cooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.Cooldown"));
    private final List<String> globalcooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.GlobalCooldown"));

    private final Boolean projectilefix = config.getBoolean("PurgeAbilities.Settings.ProjectileFix");

    private final Boolean rangeenabled = config.getBoolean("Snowport.Range.Enabled");
    private final List<String> rangemessage = CC.translate(config.getStringList("Snowport.Messages.Range"));

    private final List<String> damagermessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("Snowport.Messages.Hit.Damager"));
    private final List<String> damagedmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("Snowport.Messages.Hit.Damaged"));

    private final List<String> disabledmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ItemDisabled"));
    private final Boolean enabledmessageboolean = config.getBoolean("PurgeAbilities.Settings.ItemDisabledMessage");


    @Override
    public String getName() {
        return "Snowport";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(config.getString("Snowport.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(config.getStringList("Snowport.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return config.getInt("Snowport.Cooldown");
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.SNOW_BALL);
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("Snowport.Enabled");
    }

    @Override
    public String getInfo() {
        return "The Snowport ability item is where you throw a snowball at an player. If that snowball hits the player, both players would switch positions. It's also known as a 'Switcher Ball'";
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @EventHandler
    public void projectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Snowball)) return;
        Snowball snowball = (Snowball) event.getEntity();
        if (!(snowball.getShooter() instanceof Player)) return;
        Player player = (Player) snowball.getShooter();
        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getSnowport();
        if (!isItem(player.getItemInHand())) {
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
        Cooldown globalCooldown = PurgeAbilities.getInstance().getCooldowns().getGlobalCooldown();
        if (globalCooldown.onCooldown(player)) {
            for (String s : globalcooldownmessage) {
                player.sendMessage(s.replace("%time%", globalCooldown.getRemaining(player)).replace("%heart%", "❤"));
            }
            event.setCancelled(true);
            if (projectilefix) {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() + 1);
            }
            player.updateInventory();
            return;
        }
        if (cooldown.onCooldown(player)) {
            for (String s : cooldownmessage) {
                player.sendMessage(s.replace("%time%", cooldown.getRemaining(player)).replace("%heart%", "❤"));
            }
            event.setCancelled(true);
            if (projectilefix) {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() + 1);
                player.updateInventory();
            }
            return;
        }
        snowball.setMetadata("snowport", new FixedMetadataValue(PurgeAbilities.getInstance(), player.getUniqueId()));
        cooldown.applyCooldown(player, getCooldown());
        if (PurgeAbilities.getInstance().getGlobalcooldownB()) {
            globalCooldown.applyCooldown(player, PurgeAbilities.getInstance().getGlobalcooldowntime());
            if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.GlobalCooldown.RunCommand")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.GlobalCooldown.Command"));
            }

        }
        if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.Command.Enabled")) {
                       Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.Command.Command").replace("%ability%", getDisplayName()).replace("%time%", Integer.toString(getCooldown())).replace("%player%", player.getName()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (!(event.getDamager() instanceof Snowball))
            return;
        if (event.isCancelled()) return;
        Snowball snowball = (Snowball) event.getDamager();
        if (!(snowball.getShooter() instanceof Player) && !(event.getEntity() instanceof Player))
            return;
        if (!snowball.hasMetadata("snowport"))
            return;
        Player damager = (Player) snowball.getShooter();
        Player damaged = (Player) event.getEntity();
        Location loc = damager.getLocation();
        if (rangeenabled) {
            if (damaged.getLocation().distance(damager.getLocation()) > PurgeAbilities.getInstance().getConfig()
                    .getInt("Snowport.Range.Range")) {
                for (String s : rangemessage) {
                    damager.sendMessage(s.replace("%heart%", "❤").replace("%player%", damaged.getName()));
                }
                event.setCancelled(true);
                return;
            }
            damager.teleport(damaged);
            damaged.teleport(loc);
            if (config.getBoolean(getName() + ".Sound.Enabled")) {
                damaged.playSound(damaged.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
            }
            if (config.getBoolean(getName() + ".Sound.Enabled")) {
                damager.playSound(damager.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
            }
            for (String s : damagedmessage) {
                damaged.sendMessage(s.replace("%player%", damager.hasPotionEffect(PotionEffectType.INVISIBILITY) ? CC.translate(config.getString("PurgeAbilities.Prefixes.InvisibilityPrefix")) : damager.getName()).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
            }
            for (String s : damagermessage) {
                damager.sendMessage(s.replace("%player%"
                        , damaged.hasPotionEffect(PotionEffectType.INVISIBILITY)
                                ? CC.translate(config.getString("PurgeAbilities.Prefixes.InvisibilityPrefix"))
                                : damaged.getName()).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
            }
        } else {
            damager.teleport(damaged);
            damaged.playSound(damaged.getLocation(), Sound.valueOf(config.getString("Snowport.Sound")), 1f, 1f);
            damager.playSound(damager.getLocation(), Sound.valueOf(config.getString("Snowport.Sound")), 1f, 1f);
            damaged.teleport(loc);
            for (String s : damagedmessage) {
                damaged.sendMessage(s.replace("%player%", damager.hasPotionEffect(PotionEffectType.INVISIBILITY) ? CC.translate(config.getString("PurgeAbilities.Prefixes.InvisibilityPrefix")) : damager.getName()).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
            }
            for (String s : damagermessage) {
                damager.sendMessage(s.replace("%player%", damaged.hasPotionEffect(PotionEffectType.INVISIBILITY) ? CC.translate(config.getString("PurgeAbilities.Prefixes.InvisibilityPrefix")) : damaged.getName()).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
            }
        }

    }
}


