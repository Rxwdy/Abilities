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
        names = {"info"},
        permission = "purge.abilities.info",
        description = "Displays a list of all the abilities",
        usage = "info <ability>"
)
public class AbilityInfoArgument extends CommandArgument implements CommandCompleter {

    private final List<String> invalidabilitymessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.InvalidAbility"));

    private PurgeAbilities instance;

    public AbilityInfoArgument(PurgeAbilities instance) {
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(CC.createUsage(label, "info <ability>"));
            return;
        }

        AbilityProvider abilityProvider = PurgeAbilities.getInstance().getAbilityManager().getByName(args[1]);
        if (abilityProvider == null) {
            for (String s : invalidabilitymessage) {
                sender.sendMessage(s);
            }
            return;
        }
        final ArrayList<String> infom = new ArrayList<>();
        infom.add(CC.CHAT_BAR);
        infom.add(CC.translate(""));
        infom.add(CC.translate("&6&l" + abilityProvider.getName() + " &6&lInfo"));
        infom.add(CC.translate(""));
        infom.add(CC.translate("&6&lInfo&f: "));
        infom.add(CC.translate(abilityProvider.getInfo()));
        infom.add("");
        infom.add(CC.translate("&6&lStatus&f: ") + CC.translate(abilityProvider.isEnabled() ? "&aEnabled" : "&cDisabled"));
        infom.add(CC.translate(""));
        infom.add(CC.CHAT_BAR);

        for (String s : infom) {
            sender.sendMessage(s);
        }

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
