package services.coral.ability.commands.partneritems;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.command.CommandInfo;
import services.coral.ability.utils.command.CommandWrapper;
import services.coral.ability.utils.command.argument.CommandExecutor;

@CommandInfo(names = {"partneritems"}, permission = "")
public class PartnerItemsCommand extends CommandWrapper {

    public PartnerItemsCommand(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cThis command is for players"));
            return;
        }
        new PartnerItemsMenu().openMenu((Player) sender);
    }

}
