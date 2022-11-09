package services.coral.ability.commands.partnerpackage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.commands.partnerpackage.arguments.PackageGiveArgument;
import services.coral.ability.utils.command.CommandInfo;
import services.coral.ability.utils.command.argument.CommandExecutor;

@CommandInfo(names = {"partnerpackage", "package", "pp"}, permission = "purge.abilities.partnerpackages", helpTitle = "Partner Packages")
public class PackageCommand extends CommandExecutor {


    public PackageCommand(PurgeAbilities instance) {
        super(instance);
        this.addArgument(new PackageGiveArgument(instance));
    }

    @Override
    public boolean executeOther(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
