package services.coral.ability.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.Cooldown;

import java.util.List;

public class Rocket extends AbilityProvider {

    private final FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> globalcooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.GlobalCooldown"));

    private final List<String> disabledmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ItemDisabled"));
    private final Boolean enabledmessageboolean = config.getBoolean("PurgeAbilities.Settings.ItemDisabledMessage");

    private final List<String> cooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.Cooldown"));
    private final List<String> rocketmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("Refill.Message"));

    @Override
    public String getName() {
        return "Rocket";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(config.getString("Rocket.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(config.getStringList("Rocket.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return config.getInt("Rocket.Cooldown");
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.valueOf(config.getString("Rocket.Item.Material")));
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("Rocket.Enabled");
    }

    @Override
    public String getInfo() {
        return "The Rocket ability launches you in the air when you right-click it.";
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }
        if (!isItem(player.getItemInHand())) {
            return;
        }
        event.setCancelled(true);
        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getRocket();
        if (!isEnabled()) {
            if (enabledmessageboolean) {
                for (String s : disabledmessage) {
                    player.sendMessage(s.replace("%ability%", getDisplayName()).replace("%heart%", "❤"));
                }
                event.setCancelled(true);
            }
            return;
        }
        Cooldown globalCooldown = PurgeAbilities.getInstance().getCooldowns().getGlobalCooldown();
        if (globalCooldown.onCooldown(player)) {
            for (String s : globalcooldownmessage) {
                player.sendMessage(s.replace("%time%", globalCooldown.getRemaining(player)).replace("%heart%", "❤"));
            }
            return;
        }
        if (cooldown.onCooldown(player)) {
            for (String s : cooldownmessage) {
                player.sendMessage(s.replace("%time%", cooldown.getRemaining(player)).replace("%heart%", "❤"));
            }
            return;
        }

        for (String s : rocketmessage) {
            player.sendMessage(s.replace("%heart%", "❤").replace("%ability%", getDisplayName()));
        }


        if (PurgeAbilities.getInstance().getGlobalcooldownB()) {
            globalCooldown.applyCooldown(player, PurgeAbilities.getInstance().getGlobalcooldowntime());
            if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.GlobalCooldown.RunCommand")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.GlobalCooldown.Command"));
            }

        }
        if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.Command.Enabled")) {
                       Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.Command.Command").replace("%ability%", getDisplayName()).replace("%time%", Integer.toString(getCooldown())).replace("%player%", player.getName()));
        }
        cooldown.applyCooldown(player, getCooldown());
        if (player.getInventory().getItemInHand().getAmount() == 1) {
            player.getInventory().setItemInHand(null);
            return;
        }
        if (config.getBoolean(getName() + ".Sound.Enabled")) {
            player.playSound(player.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
        }
        player.setVelocity(new Vector(player.getLocation().getDirection().getX() * PurgeAbilities.getInstance().getConfig().getInt("Rocket.Boost.ZandX"), PurgeAbilities.getInstance().getConfig().getInt("Rocket.Boost.Y"),
                player.getLocation().getDirection().getZ() * PurgeAbilities.getInstance().getConfig().getInt("Rocket.Boost.ZandX")));
        event.setCancelled(true);
        player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
        player.updateInventory();
        event.setCancelled(true);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getCause().equals(EntityDamageEvent.DamageCause.FALL))) return;
        Player player = (Player) event.getEntity();
        if (PurgeAbilities.getInstance().getCooldowns().getRocket().onCooldown(player)) {
            event.setCancelled(true);
        }
    }

}
