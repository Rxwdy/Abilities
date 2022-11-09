package services.coral.ability.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.Cooldown;

import java.util.List;

public class OPPearl extends AbilityProvider {

    private final FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> disabledmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ItemDisabled"));
    private final Boolean enabledmessageboolean = config.getBoolean(CC.translate("PurgeAbilities.Settings.ItemDisabledMessage"));

    private final List<String> oppearlmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("OPPearl.Message"));


    private final List<String> cooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.Cooldown"));
    private final List<String> globalcooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.GlobalCooldown"));

    @Override
    public String getName() {
        return "OPPearl";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(config.getString("OPPearl.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(config.getStringList("OPPearl.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.ENDER_PEARL);
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("OPPearl.Enabled");
    }

    @Override
    public String getInfo() {
        return "The OPPearl ability item when thrown, it will give the player buff effects when landing!";
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) return;
        EnderPearl enderpearl = (EnderPearl) event.getEntity();
        Player player = (Player) enderpearl.getShooter();
        if (!isItem(player.getItemInHand())) {
            return;
        }

        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getOppearl();
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
            event.setCancelled(true);
            return;
        }
        enderpearl.setMetadata("oppearl", new FixedMetadataValue(PurgeAbilities.getPlugin(PurgeAbilities.class), player.getUniqueId()));
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
    public void on(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) return;
        EnderPearl enderpearl = (EnderPearl) event.getEntity();
        Player player = (Player) enderpearl.getShooter();
            if (enderpearl.hasMetadata("oppearl")) {
                for (String key : PurgeAbilities.getInstance().getConfig().getConfigurationSection("OPPearl.Effects").getKeys(false)) {
                    PotionEffect ps = new PotionEffect(PotionEffectType.getByName(config.getString("OPPearl.Effects." + key + ".Effect")), config.getInt("OPPearl.Effects." + key + ".Time") * 20, config.getInt("OPPearl.Effects." + key + ".Level") - 1);
                    player.addPotionEffect(ps);
                }
                if (config.getBoolean(getName() + ".Sound.Enabled")) {
                    player.playSound(player.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
                }
                for (String s : oppearlmessage) {
                    player.sendMessage(CC.translate(s).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
                }
            }
    }
}