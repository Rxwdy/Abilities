package services.coral.ability.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.Cooldown;
import services.coral.ability.utils.PartnerPackageConfig;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PartnerPackageListener implements Listener {

    private final List<String> cooldownMessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.Cooldown.Cooldown"));

    private final List<String> packagelore = CC.translate(PartnerPackageConfig.getConfig().getStringList("PartnerPackage.Item.Lore"));

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final List<String> packageCommands = PartnerPackageConfig.getConfig().getStringList("PartnerPackage.Rewards.Command");
        final int rewardAmount = PartnerPackageConfig.getConfig().getInt("PartnerPackage.Rewards.Amount");
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            return;
        }
        if (player.getItemInHand().getType() != Material.ENDER_CHEST) {
            return;
        }
        if (!player.getItemInHand().getItemMeta().getLore().equals(packagelore)) {
            return;
        }
        Cooldown cooldown = PurgeAbilities.getInstance().getCooldowns().getPackageCooldown();
        if (cooldown.onCooldown(player)) {
            for (String s : cooldownMessage) {
                player.sendMessage(s.replace("%time%", cooldown.getRemaining(player)));
            }
            return;
        }

        if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.Command.Enabled")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Settings.Command.Command").replace("%ability%", PartnerPackageConfig.getConfig().getString(CC.translate("PartnerPackage.Name"))).replace("%time%", Integer.toString(PartnerPackageConfig.getConfig().getInt("PartnerPackage.Delay"))));
        }
        PurgeAbilities.getInstance().getCooldowns().getPackageCooldown().applyCooldown(player, PartnerPackageConfig.getConfig().getInt("PartnerPackage.Delay"));
        for (String s : randomItems(packageCommands, rewardAmount)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", player.getName()));
            player.updateInventory();
        }
        player.updateInventory();
        if (player.getInventory().getItemInHand().getAmount() == 1) {
            player.getInventory().setItemInHand(null);
            return;
        }
        player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
        player.updateInventory();
    }


    public List<String> randomItems(List<String> list,
                                         int totalItems) {
        Random rand = new Random();

        List<String> newList = new ArrayList<>();
        for (int i = 0; i < totalItems; i++) {
            int randomIndex = rand.nextInt(list.size());
            newList.add(list.get(randomIndex));
            list.remove(randomIndex);
        }
        return newList;
    }

}
