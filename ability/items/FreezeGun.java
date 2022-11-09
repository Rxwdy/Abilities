package services.coral.ability.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.Cooldown;

import java.util.List;

public class FreezeGun extends AbilityProvider {

    private final FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> disabledmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ItemDisabled"));
    private final Boolean enabledmessageboolean = config.getBoolean(CC.translate("PurgeAbilities.Settings.ItemDisabledMessage"));

    private final List<String> freezegundamager = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("FreezeGun.Messages.Damager"));
    private final List<String> freezegundamaged = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("FreezeGun.Messages.Damaged"));


    private final List<String> cooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.Cooldown"));
    private final List<String> globalcooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.GlobalCooldown"));

    @Override
    public String getName() {
        return "FreezeGun";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(config.getString("FreezeGun.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(config.getStringList("FreezeGun.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return config.getInt("FreezeGun.Cooldown");
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.valueOf(config.getString("FreezeGun.Item.Material")));
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("FreezeGun.Enabled");
    }

    @Override
    public String getInfo() {
        return "The FreezeGun ability item gives the damaged player debuff effects when they have been shot by this gun!";
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }
        if (!isItem(player.getItemInHand())) {
            return;
        }

        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getFreezegun();

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
            return;
        }
        if (cooldown.onCooldown(player)) {
            for (String s : cooldownmessage) {
                player.sendMessage(s.replace("%time%", cooldown.getRemaining(player)).replace("%heart%", "❤"));
            }
            return;
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
        Snowball sb = player.launchProjectile(Snowball.class);

        sb.setMetadata("freezegun you dumbass CONNER", new FixedMetadataValue(PurgeAbilities.getInstance(), player.getUniqueId()));


    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (!(event.getDamager() instanceof Snowball))
            return;
        if (event.isCancelled()) return;
        Snowball snowball = (Snowball) event.getDamager();
        Player damager = (Player) snowball.getShooter();
        Player damaged = (Player) event.getEntity();
        if (!(snowball.getShooter() instanceof Player) && !(event.getEntity() instanceof Player))
            return;
        if (!snowball.hasMetadata("freezegun you dumbass CONNER")) {
            return;
        }
        if (config.getBoolean(getName() + ".Sound.Enabled")) {
            damager.playSound(damager.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
        }
        if (config.getBoolean(getName() + ".Sound.Enabled")) {
            damaged.playSound(damaged.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
        }
        for (String s : freezegundamager) {
            damager.sendMessage(CC.translate(s).replace("%heart%", "❤").replace("%player%", damaged.getName()).replace("%ability%", getDisplayName()));
        }
        for (String s : freezegundamaged) {
            damaged.sendMessage(CC.translate(s).replace("%heart%", "❤").replace("%player%", damager.getName()).replace("%ability%", getDisplayName()));
        }
        for (String key : PurgeAbilities.getInstance().getConfig().getConfigurationSection("FreezeGun.Effects").getKeys(false)) {
            PotionEffect ps = new PotionEffect(PotionEffectType.getByName(config.getString("FreezeGun.Effects." + key + ".Effect")), config.getInt("FreezeGun.Effects." + key + ".Time") * 20, config.getInt("FreezeGun.Effects." + key + ".Level") - 1);
            damaged.addPotionEffect(ps);
        }

    }
}
