package services.coral.ability.commands.ability;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.commands.ability.arguments.*;
import services.coral.ability.utils.command.CommandInfo;
import services.coral.ability.utils.command.argument.CommandExecutor;

@CommandInfo(names = {"ability", "pabilities", "purgeabilities"}, permission = "purge.abilities", helpTitle = "Abilities")
public class AbilityCommand extends CommandExecutor {

    public AbilityCommand(PurgeAbilities instance) {
        super(instance);
        this.addArgument(new AbilityListArgument(instance));
        this.addArgument(new AbilityGiveArgument(instance));
        this.addArgument(new AbilityReloadArgument(instance));
        this.addArgument(new AbilityToggleArgument(instance));
        this.addArgument(new AbilityResetCooldownArgument(instance));
        this.addArgument(new AbilityInfoArgument(instance));
    }

    @Override
    public boolean executeOther(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
