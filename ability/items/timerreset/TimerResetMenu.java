package services.coral.ability.items.timerreset;



import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.ItemBuilder;
import services.coral.ability.utils.menu.Button;
import services.coral.ability.utils.menu.Menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimerResetMenu extends Menu {

    FileConfiguration config = PurgeAbilities.getInstance().getConfig();

    private final List<String> usedmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("TimerReset.Messages.Used"));
    private final List<String> resetmessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("TimerReset.Messages.Reset"));

    @Override
    public String getTitle(Player player) {
        return CC.translate(config.getString("TimerReset.GUI.Name"));
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public int getSize() {
        return config.getInt("TimerReset.GUI.Size");
    }


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (String key : config.getConfigurationSection("TimerReset.GUI.Timers").getKeys(false)) {
            buttons.put(config.getInt("TimerReset.GUI.Timers." + key + ".Slot") - 1, new Button() {

                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(new ItemStack(Material.valueOf(config.getString("TimerReset.GUI.Timers." + key + ".Item.Material"))))
                            .name(CC.translate(config.getString("TimerReset.GUI.Timers." + key + ".Item.Name")))
                            .lore(CC.translate(config.getStringList("TimerReset.GUI.Timers." + key + ".Item.Lore")))
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    player.closeInventory();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.getString("TimerReset.GUI.Timers." + key + ".Command"));
                    usedmessage.forEach(s -> player.sendMessage(s.replace("%ability%", new TimerReset().getDisplayName()).replace("%heart%", "❤")));
                    resetmessage.forEach(s -> player.sendMessage(s.replace("%cooldown%", config.getString("TimerReset.GUI.Timers." + key + ".Timer"))));
                    if (config.getBoolean(new TimerReset().getName() + ".Sound.Enabled")) {
                        player.playSound(player.getLocation(), Sound.valueOf(config.getString(new TimerReset().getName() + ".Sound.Sound")), 1f, 1f);
                    }
                    PurgeAbilities.getInstance().getCooldowns().getTimerreset().applyCooldown(player, new TimerReset().getCooldown());
                    if (PurgeAbilities.getInstance().getGlobalcooldownB()) {
                        PurgeAbilities.getInstance().getCooldowns().getGlobalCooldown().applyCooldown(player, PurgeAbilities.getInstance().getGlobalcooldowntime());
                        if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.GlobalCooldown.RunCommand")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.GlobalCooldown.Command"));
                        }

                    }
                    if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.Command.Enabled")) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.Command.Command").replace("%ability%", PurgeAbilities.getInstance().getConfig().getString(CC.translate("TimerReset.Item.Name"))).replace("%time%", Integer.toString(PurgeAbilities.getInstance().getConfig().getInt("TimerReset.Cooldown"))));
                    }
                    if (player.getItemInHand().getAmount() == 0) {
                        player.setItemInHand(null);
                    } else {
                        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                    }
                }

            });
        }
        return buttons;
    }
}
