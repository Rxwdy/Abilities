package services.coral.ability.items;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.Cooldown;

import java.util.List;

public class SwitchStick extends AbilityProvider {
    private final FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> cooldownmessage = CC.translate(config.getStringList("PurgeAbilities.Messages.Cooldown.Cooldown"));
    private final List<String> globalcooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.GlobalCooldown"));

    private final List<String> damagedmessage = CC.translate(config.getStringList("SwitchStick.Messages.Hit.Damaged"));
    private final List<String> damagermessage = CC.translate(config.getStringList("SwitchStick.Messages.Hit.Damager"));

    private final List<String> disabledmessage = CC.translate(config.getStringList("PurgeAbilities.Messages.ItemDisabled"));
    private final Boolean enabledmessageboolean = config.getBoolean("PurgeAbilities.Settings.ItemDisabledMessage");

    @Override
    public String getName() {
        return "SwitchStick";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(config.getString("SwitchStick.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(config.getStringList("SwitchStick.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return config.getInt("SwitchStick.Cooldown");
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.valueOf(config.getString("SwitchStick.Item.Material")));
    }

    @Override
    public String getInfo() {
        return "The SwitchStick ability item is a item that when used to hit a player with, it will switch that player in a 180 degrees angle.";
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("SwitchStick.Enabled");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();
        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getSwitchstick();
        if (!isItem(damager.getItemInHand())) return;
        if (!isEnabled()) {
            if (enabledmessageboolean) {
                for (String s : disabledmessage) {
                    damager.sendMessage(s.replace("%ability%", getDisplayName()).replace("%heart%", "❤"));
                }
            }
        }
        Cooldown globalCooldown = PurgeAbilities.getInstance().getCooldowns().getGlobalCooldown();
        if (globalCooldown.onCooldown(damager)) {
            for (String s : globalcooldownmessage) {
                damager.sendMessage(s.replace("%time%", globalCooldown.getRemaining(damager)).replace("%heart%", "❤"));
            }
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
        cooldown.applyCooldown(damager, getCooldown());
        if (PurgeAbilities.getInstance().getGlobalcooldownB()) {
            globalCooldown.applyCooldown(damager, PurgeAbilities.getInstance().getGlobalcooldowntime());
            if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.GlobalCooldown.RunCommand")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.GlobalCooldown.Command"));
            }
            if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.Command.Enabled")) {
                           Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.Command.Command").replace("%ability%", getDisplayName()).replace("%time%", Integer.toString(getCooldown())).replace("%player%", damager.getName()));
            }

        }
        Location location = damaged.getLocation();
        location.setYaw(location.getYaw() + 180);
        damaged.teleport(location);
        if (config.getBoolean(getName() + ".Sound.Enabled")) {
            damager.playSound(damager.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
        }
        if (config.getBoolean(getName() + ".Sound.Enabled")) {
            damaged.playSound(damaged.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
        }
        for (String s : damagedmessage) {
            damaged.sendMessage(s.replace("%player%", damager.hasPotionEffect(PotionEffectType.INVISIBILITY) ? CC.translate(config.getString("PurgeAbilities.Prefixes.InvisibilityPrefix")) : damager.getName()).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
        }
        for (String s2 : damagermessage) {
            damager.sendMessage(s2.replace("%player%", damaged.hasPotionEffect(PotionEffectType.INVISIBILITY) ? CC.translate(config.getString("PurgeAbilities.Prefixes.InvisibilityPrefix")) : damaged.getName()).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
        }

    }

}
