package services.coral.ability.items;

import org.bukkit.Bukkit;
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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TimerBone extends AbilityProvider {

    HashMap<UUID, Integer> timerbone = new HashMap<UUID, Integer>();
    HashMap<UUID, UUID> otherplayer = new HashMap<UUID, UUID>();
    int a;

    private final FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> cooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.Cooldown"));
    private final List<String> globalcooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.GlobalCooldown"));

    private final List<String> timerbonemessagedamager = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("TimerBone.Messages.Damager.Message"));
    private final List<String> timerbonemessagedamaged = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("TimerBone.Messages.Damaged"));
    private final List<String> outoftimemessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("TimerBone.Messages.Damager.HitsReset"));

    private final List<String> wrongplayermessage = CC.translate(config.getStringList("TimerBone.Messages.Damager.WrongPlayer"));

    private final List<String> disabledmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ItemDisabled"));
    private final Boolean enabledmessageboolean = config.getBoolean(CC.translate("PurgeAbilities.Settings.ItemDisabledMessage"));
    
    @Override
    public String getName() {
        return "TimerBone";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(config.getString("TimerBone.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(config.getStringList("TimerBone.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return config.getInt("TimerBone.Cooldown");
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.valueOf(config.getString("TimerBone.Item.Material")));
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("TimerBone.Enabled");
    }

    @Override
    public String getInfo() {
        return "The Timerbone ability gives the damaged a enderpearl cooldown when they have been three times by the damager using the timerbone.";
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();
        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getTimerbone();
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
        try {
            a = timerbone.get(damager.getUniqueId());
            if (otherplayer.get(damager.getUniqueId()) != damaged.getUniqueId()) {
                timerbone.remove(damager.getUniqueId());
                otherplayer.remove(damager.getUniqueId());
                for (String s : wrongplayermessage) {
                    damager.sendMessage(s);
                }
                return;
            }
        } catch (NullPointerException e) {
            timerbone.put(damager.getUniqueId(), 0);
            otherplayer.put(damager.getUniqueId(), damaged.getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(PurgeAbilities.getPlugin(PurgeAbilities.class), new Runnable() {
                @Override
                public void run() {
                    if (!timerbone.containsKey(damager.getUniqueId())) return;
                    timerbone.remove(damager.getUniqueId());
                    for (String s : outoftimemessage) {
                        damager.sendMessage(s.replace("%ability%", getDisplayName()).replace("%heart%", "❤"));
                    }
                }

            }, config.getInt("TimerBone.Settings.ResetHitsAfter") * 20);
            return;
        }
        if (a == 0) {
            timerbone.put(damager.getUniqueId(), a + 1);
            return;
        }
        if (a == 1) {
            if (config.getBoolean(getName() + ".Sound.Enabled")) {
                damager.playSound(damager.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
            }
            if (config.getBoolean(getName() + ".Sound.Enabled")) {
                damaged.playSound(damaged.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
            }
            if (config.getBoolean(getName() + ".Settings.OneTimeUse")) {
                if (damager.getItemInHand().getAmount() == 1) {
                    damager.setItemInHand(null);
                } else {
                    damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
                }
            }
            for (String s : timerbonemessagedamaged) {
                damaged.sendMessage(s.replace("%player%", damager.hasPotionEffect(PotionEffectType.INVISIBILITY) ? CC.translate(config.getString("PurgeAbilities.Prefixes.InvisibilityPrefix")) : damager.getName()).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
            }
            for (String s : timerbonemessagedamager) {
                damager.sendMessage(s.replace("%player%", damaged.hasPotionEffect(PotionEffectType.INVISIBILITY) ? CC.translate(config.getString("PurgeAbilities.Prefixes.InvisibilityPrefix")) : damaged.getName()).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("TimerBone.Command").replace("%player%", damaged.getName()));
            cooldown.applyCooldown(damager, getCooldown());
            timerbone.put(damager.getUniqueId(), 0);
            if (PurgeAbilities.getInstance().getGlobalcooldownB()) {
                globalCooldown.applyCooldown(damager, PurgeAbilities.getInstance().getGlobalcooldowntime());
                if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.GlobalCooldown.RunCommand")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.GlobalCooldown.Command"));
                }
                if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.Command.Enabled")) {
                               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.Command.Command").replace("%ability%", getDisplayName()).replace("%time%", Integer.toString(getCooldown())).replace("%player%", damager.getName()));
                }

            }
            timerbone.remove(damager.getUniqueId());
            return;
        }
    }
    
}
