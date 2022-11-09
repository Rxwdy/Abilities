package services.coral.ability.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.items.pocketbard.PocketBardMenu;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.Cooldown;

import java.util.List;


public class Cocaine extends AbilityProvider {
    private final FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> cooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.Cooldown"));
    private final List<String> globalcooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.GlobalCooldown"));

    private final List<String> cocainemessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("Cocaine.Message"));

    private final List<String> disabledmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ItemDisabled"));
    private final Boolean enabledmessageboolean = config.getBoolean(CC.translate("PurgeAbilities.Settings.ItemDisabledMessage"));


    @Override
    public String getName() {
        return "Cocaine";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(PurgeAbilities.getInstance().getConfig().getString("Cocaine.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("Cocaine.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return PurgeAbilities.getInstance().getConfig().getInt("Cocaine.Cooldown");
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.valueOf(PurgeAbilities.getInstance().getConfig().getString("Cocaine.Item.Material")));
    }

    @Override
    public boolean isEnabled() {
        return PurgeAbilities.getInstance().getConfig().getBoolean("Cocaine.Enabled");
    }

    @Override
    public String getInfo() {
        return "The Cocaine ability item is a ability where you can right-click it to give you a set amount of effects. The effect given is usually speed.";
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

        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getCocaine();
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
        /*if (config.getBoolean("Cocaine.SpeedFix.Enabled")) {
            if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                    if (activePotionEffect.getType() != PotionEffectType.SPEED) {
                        continue;
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.addPotionEffect(activePotionEffect);
                        }
                    }.runTaskLaterAsynchronously(PurgeAbilities.getInstance(), config.getInt("Cocaine.SpeedFix.Delay") * 20);
                }

            }
        }*/
        for (String key : PurgeAbilities.getInstance().getConfig().getConfigurationSection("Cocaine.Effects").getKeys(false)) {
            PotionEffect ps = new PotionEffect(PotionEffectType.getByName(config.getString(getName() + ".Effects." + key + ".Effect")), config.getInt(getName() + ".Effects." + key + ".Time") * 20, config
            .getInt(getName() + ".Effects." + key + ".Level") - 1);
            player.addPotionEffect(ps);
        }
        if (config.getBoolean(getName() + ".Sound.Enabled")) {
            player.playSound(player.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
        }
        for (String s : cocainemessage) {
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
        player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
        player.updateInventory();
    }
}
