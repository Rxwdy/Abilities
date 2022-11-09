package services.coral.ability.commands.ability.arguments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.command.CommandInfo;
import services.coral.ability.utils.command.argument.CommandArgument;


@CommandInfo(
        names = {"reload"},
        permission = "purge.abilities.reload",
        description = "Reloads the configuration file",
        usage = "reload"
)
public class AbilityReloadArgument extends CommandArgument {

    private PurgeAbilities instance;

    public AbilityReloadArgument(PurgeAbilities instance) {
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return;
        }
        PurgeAbilities.getInstance().reloadConfig();
        sender.sendMessage(CC.translate("&aThe config.yml has been reloaded."));
    }
}
