package services.coral.ability.commands.ability.arguments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.command.CommandCompleter;
import services.coral.ability.utils.command.CommandInfo;
import services.coral.ability.utils.command.argument.CommandArgument;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        names = {"toggle"},
        permission = "purge.abilities.toggle",
        description = "Toggles an ability item on or off",
        usage = "toggle <ability>"
)
public class AbilityToggleArgument extends CommandArgument implements CommandCompleter {

    private final List<String> toggleenablemessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("CoralAbilities.Messages.ItemStatus.Enabled"));
    private final List<String> toggledisablemessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("CoralAbilities.Messages.ItemStatus.Disabled"));

    private final List<String> invalidabilitymessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("CoralAbilities.Messages.InvalidAbility"));

    private PurgeAbilities instance;

    public AbilityToggleArgument(PurgeAbilities instance) {
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(CC.createUsage(label, "toggle <ability>"));
            return;
        }
        AbilityProvider abilityProvider = PurgeAbilities.getInstance().getAbilityManager().getByName(args[1]);
        if (abilityProvider == null) {
            for (String s : invalidabilitymessage) {
                sender.sendMessage(s);
            }
            return;
        }
        if (abilityProvider.isEnabled()) {
            PurgeAbilities.getInstance().getConfig().set(abilityProvider.getName() + ".Enabled", false);
            for (String s : toggledisablemessage) {
                sender.sendMessage(s.replace("%ability%", abilityProvider.getName()));
            }
        } else {
            PurgeAbilities.getInstance().getConfig().set(abilityProvider.getName() + ".Enabled", true);
            for (String s : toggleenablemessage) {
                sender.sendMessage(s.replace("%ability%", abilityProvider.getName()));
            }
        }
        PurgeAbilities.getInstance().saveConfig();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> abilities = new ArrayList<>();
        if (args.length == 2) {
            for (AbilityProvider ability : PurgeAbilities.getInstance().getAbilityManager().getAbilities()) {
                abilities.add(ability.getName());
            }
        }
        return abilities;
    }
}
