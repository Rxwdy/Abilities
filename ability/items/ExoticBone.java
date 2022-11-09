package services.coral.ability.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.Cooldown;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ExoticBone extends AbilityProvider {

    HashMap<UUID, Integer> exoticbone = new HashMap<UUID, Integer>();
    HashMap<UUID, UUID> otherplayer = new HashMap<UUID, UUID>();
    int a;

    private final FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> cooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.Cooldown"));
    private final List<String> globalcooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.GlobalCooldown"));

    private final List<String> wrongplayermessage = CC.translate(config.getStringList("ExoticBone.Messages.Damager.WrongPlayer"));

    private final List<String> exoticbonemessagedamager = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("ExoticBone.Messages.Damager.Message"));
    private final List<String> exoticbonemessagedamaged = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("ExoticBone.Messages.Damaged.Message"));
    private final List<String> outoftimemessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("ExoticBone.Messages.Damager.HitsReset"));


    private final List<String> disabledmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ItemDisabled"));
    private final Boolean enabledmessageboolean = config.getBoolean(CC.translate("PurgeAbilities.Settings.ItemDisabledMessage"));

    @Override
    public String getName() {
        return "ExoticBone";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(config.getString("ExoticBone.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(config.getStringList("ExoticBone.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return config.getInt("ExoticBone.Cooldown");
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.valueOf(config.getString("ExoticBone.Item.Material")));
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("ExoticBone.Enabled");
    }

    @Override
    public String getInfo() {
        return "The ExoticBone ability stops the damaged player from interacting with blocks, gates, and doors. When hit three times by the damager.";
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
        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getExoticbone();
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
            a = exoticbone.get(damager.getUniqueId());
            if (otherplayer.get(damager.getUniqueId()) != damaged.getUniqueId()) {
                exoticbone.remove(damager.getUniqueId());
                otherplayer.remove(damager.getUniqueId());
                for (String s : wrongplayermessage) {
                    damager.sendMessage(s);
                }
                return;
            }
        } catch (NullPointerException e) {
            exoticbone.put(damager.getUniqueId(), 0);
            otherplayer.put(damager.getUniqueId(), damaged.getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(PurgeAbilities.getPlugin(PurgeAbilities.class), new Runnable() {
                @Override
                public void run() {
                    if (!exoticbone.containsKey(damager.getUniqueId())) return;
                    exoticbone.remove(damager.getUniqueId());
                    for (String s : outoftimemessage) {
                            damager.sendMessage(s.replace("%ability%", getDisplayName()).replace("%heart%", "❤"));
                        }
                    }

            }, config.getInt("ExoticBone.Settings.ResetHitsAfter") * 20);
            return;
        }
        if (a == 0) {
            exoticbone.put(damager.getUniqueId(), a + 1);
            return;
        }
        if (a == 1) {
            for (String s : exoticbonemessagedamaged) {
                damaged.sendMessage(s.replace("%player%", damager.hasPotionEffect(PotionEffectType.INVISIBILITY) ? CC.translate(config.getString("PurgeAbilities.Prefixes.InvisibilityPrefix")) : damager.getName()).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
            }
            for (String s : exoticbonemessagedamager) {
                damager.sendMessage(s.replace("%player%", damaged.hasPotionEffect(PotionEffectType.INVISIBILITY) ? CC.translate(config.getString("PurgeAbilities.Prefixes.InvisibilityPrefix")) : damaged.getName()).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
            }
            if (config.getBoolean(getName() + ".Sound.Enabled")) {
                damaged.playSound(damaged.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
            }
            if (config.getBoolean(getName() + ".Sound.Enabled")) {
                damager.playSound(damager.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
            }
            if (config.getBoolean(getName() + ".Settings.OneTimeUse")) {
                if (damager.getItemInHand().getAmount() == 1) {
                    damager.setItemInHand(null);
                } else {
                    damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
                }
            }
            cooldown.applyCooldown(damager, getCooldown());
            PurgeAbilities.getInstance().getCooldowns().getExoticbonedamaged().applyCooldown(damaged, PurgeAbilities.getInstance().getConfig().getInt("ExoticBone.Settings.Interact.Time"));
            exoticbone.put(damager.getUniqueId(), 0);
            if (PurgeAbilities.getInstance().getGlobalcooldownB()) {
                globalCooldown.applyCooldown(damaged, PurgeAbilities.getInstance().getGlobalcooldowntime());
                if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.GlobalCooldown.RunCommand")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.GlobalCooldown.Command"));
                }

            }
            if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.Command.Enabled")) {
                           Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.Command.Command").replace("%ability%", getDisplayName()).replace("%time%", Integer.toString(getCooldown())).replace("%player%", damager.getName()));
            }
            exoticbone.remove(damager.getUniqueId());
            return;
        }
    }

    private final List<String> nointeractmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("ExoticBone.Messages.Damaged.Interact"));
    private final Boolean doorsgates = config.getBoolean(CC.translate("ExoticBone.Settings.Interact.Doors-Gates"));
    private final Boolean leversbuttons = config.getBoolean(CC.translate("ExoticBone.Settings.Interact.Levers-Buttons"));


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR) return;
        if (event.getAction() == Action.RIGHT_CLICK_AIR) return;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        if (!PurgeAbilities.getInstance().getCooldowns().getExoticbonedamaged().onCooldown(player)) return;
        if (block.getType().equals(Material.FENCE_GATE) || block.getType().equals(Material.TRAP_DOOR) || block.getType().equals(Material.WOODEN_DOOR) || block.getType().equals(Material.WOOD_DOOR)) {
            if (!doorsgates) {
                event.setCancelled(true);
                for (String s : nointeractmessage) {
                    player.sendMessage(s.replace("%time%", PurgeAbilities.getInstance().getCooldowns().getExoticbonedamaged().getRemaining(player)).replace("%heart%", "❤"));
                }
            }
        }
        if (block.getType().equals(Material.LEVER) || block.getType().equals(Material.STONE_BUTTON) || block.getType().equals(Material.WOOD_BUTTON)) {
            if (!leversbuttons) {
                event.setCancelled(true);
                for (String s : nointeractmessage) {
                    player.sendMessage(s.replace("%time%", PurgeAbilities.getInstance().getCooldowns().getExoticbonedamaged().getRemaining(player)).replace("%heart%", "❤"));
                }
            }
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!config.getBoolean("ExoticBone.Settings.Interact.Place")) {
            if (!PurgeAbilities.getInstance().getCooldowns().getExoticbonedamaged().onCooldown(player)) {
                return;
            }

            event.setCancelled(true);
            for (String s : nointeractmessage) {
                player.sendMessage(s.replace("%time%", PurgeAbilities.getInstance().getCooldowns().getExoticbonedamaged().getRemaining(player)).replace("%heart%", "❤"));
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!config.getBoolean("ExoticBone.Settings.Interact.Break")) {
            if (!PurgeAbilities.getInstance().getCooldowns().getExoticbonedamaged().onCooldown(player)) return;
            event.setCancelled(true);
            for (String s : nointeractmessage) {
                player.sendMessage(s.replace("%time%", PurgeAbilities.getInstance().getCooldowns().getExoticbonedamaged().getRemaining(player)).replace("%heart%", "❤"));
            }
        }
    }
}