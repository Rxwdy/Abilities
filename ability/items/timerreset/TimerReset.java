package services.coral.ability.items.timerreset;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.Cooldown;

import java.util.List;

public class TimerReset extends AbilityProvider {

    FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> cooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.Cooldown"));
    private final List<String> globalcooldownmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.GlobalCooldown"));


    private final List<String> disabledmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ItemDisabled"));
    private final Boolean enabledmessageboolean = config.getBoolean(CC.translate("PurgeAbilities.Settings.ItemDisabledMessage"));

    @Override
    public String getName() {
        return "TimerReset";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(config.getString("TimerReset.Item.Name"));
    }

    @Override
    public List<String> getLore() {
        return CC.translate(config.getStringList("TimerReset.Item.Lore"));
    }

    @Override
    public int getCooldown() {
        return config.getInt("TimerReset.Cooldown");
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.valueOf(config.getString("TimerReset.Item.Material")));
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("TimerReset.Enabled");
    }

    @Override
    public String getInfo() {
        return "Upon right clicking this item, a gui will appear with all the timers in your config. Upon clicking the timer, it will execute the command given in the config.";
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
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }
        if (!isItem(player.getItemInHand())) {
            return;
        }
        if (!isEnabled()) {
            if (enabledmessageboolean) {
                for (String s : disabledmessage) {
                    player.sendMessage(s.replace("%ability%", getDisplayName()));
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
        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getTimerreset();
        if (cooldown.onCooldown(player)) {
            for (String s : cooldownmessage) {
                player.sendMessage(s.replace("%time%", cooldown.getRemaining(player)).replace("%heart%", "❤"));
            }
            return;
        }
        new TimerResetMenu().openMenu(player);
    }
}
