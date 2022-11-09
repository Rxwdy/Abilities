package services.coral.ability.commands.partnerpackage.arguments;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.PartnerPackageConfig;
import services.coral.ability.utils.command.CommandInfo;
import services.coral.ability.utils.command.argument.CommandArgument;

import java.util.List;

@CommandInfo(
        names = {"give"},
        permission = "purge.abilities.partnerpackages.give",
        description = "Gives a partnerpackage to a player",
        usage = "give <player/ALL> <amount>"
)
public class PackageGiveArgument extends CommandArgument {

    private PurgeAbilities instance;

    private final List<String> invalidplayer = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.InvalidPlayer"));
    private final List<String> invalidamount = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.InvalidAmount"));

    public PackageGiveArgument(PurgeAbilities instance) {
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3) {
            CC.createUsage(label, "give <player/All> <amount>");
            return;
        }
        try {
            ItemStack itemStack = new ItemStack(Material.ENDER_CHEST, Integer.parseInt(args[2]));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(CC.translate(PartnerPackageConfig.getConfig().getString("PartnerPackage.Item.Name")));
            itemMeta.setLore(CC.translate(PartnerPackageConfig.getConfig().getStringList("PartnerPackage.Item.Lore")));
            itemStack.setItemMeta(itemMeta);
            if (args[1].equalsIgnoreCase("ALL")) {
                for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                    for (ItemStack value : onlinePlayer.getInventory().addItem(itemStack).values()) {
                        onlinePlayer.getWorld().dropItemNaturally(onlinePlayer.getLocation(), value);
                    }
                }
                return;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                for (String s : invalidplayer) {
                    sender.sendMessage(s);
                }
                return;
            }
            for (ItemStack value : target.getInventory().addItem(itemStack).values()) {
                target.getWorld().dropItemNaturally(target.getLocation(), value);
            }
        } catch(NumberFormatException e) {

        }

    }

}
