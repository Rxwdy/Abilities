package services.coral.ability.items.pocketbard;

import me.hulipvp.hcf.api.HCFAPI;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.Cooldown;

import java.util.List;

public class PocketBard extends AbilityProvider {

    private final FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> disabledmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ItemDisabled"));
    private final Boolean enabledmessageboolean = config.getBoolean(CC.translate("PurgeAbilities.Settings.ItemDisabledMessage"));

    private final List<String> pocketbardmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PocketBard.Message"));


    private final List<String> cooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.Cooldown"));
    private final List<String> globalcooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.GlobalCooldown"));

    @Override
    public String getName() {
        return "PocketBard";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(config.getString("PocketBard.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(config.getStringList("PocketBard.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return config.getInt("PocketBard.Cooldown");
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.valueOf(PurgeAbilities.getInstance().getConfig().getString("PocketBard.Item.Material.Material")), 1, (byte) PurgeAbilities.getInstance().getConfig().getInt("PocketBard.Item.Material.Byte"));
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("PocketBard.Enabled");
    }

    @Override
    public String getInfo() {
        return "The PocketBard ability opens a GUI when clicked, you get 4 options of effects to pick from. Once you pick an option, you will receive that item. When you click on the item you have received, you will gain its effects.";
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

        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getPocketbard();
        if (!isEnabled()) {
            if (enabledmessageboolean) {
                for (String s : disabledmessage) {
                    player.sendMessage(s.replace("%ability%", getDisplayName()).replace("%heart%", "❤"));
                }
            }
            return;
        }
        Cooldown globalCooldown = PurgeAbilities.getInstance().getCooldowns().getGlobalCooldown();
        new PocketBardMenu().openMenu(player);
    }

    @EventHandler
    public void onStrength(PlayerInteractEvent event) {
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
        if (player.getItemInHand() == null) return;
        if (player.getItemInHand().getType() != Material.BLAZE_POWDER) return;
        if (!player.getItemInHand().hasItemMeta()) return;
        if (!player.getItemInHand().getItemMeta().getLore().equals(CC.translate(config.getStringList("PocketBard.Menu.Items.Strength.Lore"))))
            return;
        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getPocketbard();
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

        if (PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Custom").equals("NicksHCFCore")) {
            if (!HCFAPI.hasFaction(player)) return;
            for (Player all : HCFAPI.getOnlineTeamMembers(player)) {
                all.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, config.getInt("PocketBard.Menu.Items.Strength.Effect.Strength.Time") * 20, config.getInt("PocketBard.Menu.Items.Strength.Effect.Strength.Level") - 1));

            }
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, config.getInt("PocketBard.Menu.Items.Strength.Effect.Strength.Time") * 20, config.getInt("PocketBard.Menu.Items.Strength.Effect.Strength.Level") - 1));
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

        for (String s : pocketbardmessage) {
            player.sendMessage(CC.translate(s).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
        }
        if (config.getBoolean(getName() + ".Sound.Enabled")) {
            player.playSound(player.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
        }
        if (player.getInventory().getItemInHand().getAmount() == 1) {
            player.getInventory().setItemInHand(null);
            return;
        }
        player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
        player.updateInventory();

    }

    @EventHandler
    public void onResistance(PlayerInteractEvent event) {
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
        if (player.getItemInHand() == null) return;
        if (player.getItemInHand().getType() != Material.IRON_INGOT) return;
        if (!player.getItemInHand().hasItemMeta()) return;
        if (!player.getItemInHand().getItemMeta().getLore().equals(CC.translate(config.getStringList("PocketBard.Menu.Items.Resistance.Lore"))))
            return;
        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getPocketbard();
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
        cooldown.applyCooldown(player, getCooldown());
        for (Player all : HCFAPI.getOnlineTeamMembers(player)) {
            all.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, config.getInt("PocketBard.Menu.Items.Resistance.Effect.Resistance.Time") * 20, config.getInt("PocketBard.Menu.Items.Resistance.Effect.Resistance.Level") - 1));

        }
        if (PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Custom").equals("NicksHCFCore")) {
            if (!HCFAPI.hasFaction(player)) return;
            for (Player all : HCFAPI.getOnlineTeamMembers(player)) {
                all.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, config.getInt("PocketBard.Menu.Items.Resistance.Effect.Resistance.Time") * 20, config.getInt("PocketBard.Menu.Items.Resistance.Effect.Resistance.Level") - 1));

            }
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, config.getInt("PocketBard.Menu.Items.Resistance.Effect.Resistance.Time") * 20, config.getInt("PocketBard.Menu.Items.Resistance.Effect.Resistance.Level") - 1));
        if (PurgeAbilities.getInstance().getGlobalcooldownB()) {
            globalCooldown.applyCooldown(player, PurgeAbilities.getInstance().getGlobalcooldowntime());
            if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.GlobalCooldown.RunCommand")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.GlobalCooldown.Command"));
            }

        }
        if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.Command.Enabled")) {
                       Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.Command.Command").replace("%ability%", getDisplayName()).replace("%time%", Integer.toString(getCooldown())).replace("%player%", player.getName()));
        }
        if (config.getBoolean(getName() + ".Sound.Enabled")) {
            player.playSound(player.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
        }
        for (String s : pocketbardmessage) {
            player.sendMessage(CC.translate(s).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
        }

        int amount = player.getInventory().getItemInHand().getAmount();
        if (amount == 1) {
            player.getInventory().setItemInHand(null);
            return;
        }
        player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
        player.updateInventory();

    }

    @EventHandler
    public void onRegen(PlayerInteractEvent event) {
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
        if (player.getItemInHand() == null) return;
        if (player.getItemInHand().getType() != Material.GHAST_TEAR) return;
        if (!player.getItemInHand().hasItemMeta()) return;
        if (!player.getItemInHand().getItemMeta().getLore().equals(CC.translate(config.getStringList("PocketBard.Menu.Items.Regeneration.Lore"))))
            return;
        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getPocketbard();
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
        if (PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Custom").equals("NicksHCFCore")) {
            if (!HCFAPI.hasFaction(player)) return;
                for (Player all : HCFAPI.getOnlineTeamMembers(player)) {
                    all.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, config.getInt("PocketBard.Menu.Items.Regeneration.Effect.Regeneration.Time") * 20, config.getInt("PocketBard.Menu.Items.Regeneration.Effect.Regeneration.Level") - 1));

                }
            }
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, config.getInt("PocketBard.Menu.Items.Regeneration.Effect.Regeneration.Time") * 20, config.getInt("PocketBard.Menu.Items.Regeneration.Effect.Regeneration.Level") - 1));


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
        if (config.getBoolean(getName() + ".Sound.Enabled")) {
            player.playSound(player.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
        }
        for (String s : pocketbardmessage) {
            player.sendMessage(CC.translate(s).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
        }

        if (player.getInventory().getItemInHand().getAmount() == 1) {
            player.getInventory().setItemInHand(null);
            return;
        }
        player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
        player.updateInventory();

    }

    @EventHandler
    public void onJumpboost(PlayerInteractEvent event) {
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
        if (player.getItemInHand() == null) return;
        if (player.getItemInHand().getType() != Material.FEATHER) return;
        if (!player.getItemInHand().hasItemMeta()) return;
        if (!player.getItemInHand().getItemMeta().getLore().equals(CC.translate(config.getStringList("PocketBard.Menu.Items.JumpBoost.Lore"))))
            return;
        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getPocketbard();
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
        if (config.getBoolean(getName() + ".Sound.Enabled")) {
            player.playSound(player.getLocation(), Sound.valueOf(config.getString(getName() + ".Sound.Sound")), 1f, 1f);
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
        if (PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Custom").equals("NicksHCFCore")) {
            if (!HCFAPI.hasFaction(player)) return;
            for (Player all : HCFAPI.getOnlineTeamMembers(player)) {
                all.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, config.getInt("PocketBard.Menu.Items.JumpBoost.Effect.JumpBoost.Time") * 20, config.getInt("PocketBard.Menu.Items.JumpBoost.Effect.JumpBoost.Level") - 1));

            }
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, config.getInt("PocketBard.Menu.Items.JumpBoost.Effect.JumpBoost.Time") * 20, config.getInt("PocketBard.Menu.Items.JumpBoost.Effect.JumpBoost.Level") - 1));

        for (String s : pocketbardmessage) {
            player.sendMessage(CC.translate(s).replace("%heart%", "❤").replace("%ability%", getDisplayName()));
        }

        if (player.getInventory().getItemInHand().getAmount() == 1) {
            player.getInventory().setItemInHand(null);
            return;
        }
        player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
        player.updateInventory();
    }


}