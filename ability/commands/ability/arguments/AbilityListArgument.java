package services.coral.ability.commands.ability.arguments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.commands.ability.AbilityListMenu;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.command.CommandInfo;
import services.coral.ability.utils.command.argument.CommandArgument;

import java.util.ArrayList;
@CommandInfo(
        names = {"list"},
        permission = "purge.abilities.list",
        description = "Displays a list of all the abilities",
        usage = "list"
)
public class AbilityListArgument extends CommandArgument {


    private PurgeAbilities instance;

    public AbilityListArgument(PurgeAbilities instance) {
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return;
        }
        if (sender instanceof Player) {
            if (PurgeAbilities.getInstance().getConfig().getBoolean("PurgeAbilities.Settings.AbilityListGUI")) {
                new AbilityListMenu().openMenu((Player) sender);
                return;
            }
        }
        final ArrayList<String> listm = new ArrayList<>();
        listm.add(CC.CHAT_BAR);
        listm.add(CC.translate(""));
        listm.add(CC.translate("&6&lAbilities"));
        listm.add(CC.translate(""));
        listm.add(CC.translate("&6Items:"));
        for (AbilityProvider ability : PurgeAbilities.getInstance().getAbilityManager().getAbilities()) {
            if (ability.isEnabled()) {
                listm.add(CC.translate(" &7- " + PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Prefixes.ListFormat")
                        .replace("%ability%", ability.getName())
                        .replace("%prefix%", CC.translate(PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Prefixes.EnabledPrefix")))));
                continue;
            }
            listm.add(CC.translate(" &7- " + PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Prefixes.ListFormat")
                    .replace("%ability%", ability.getName())
                    .replace("%prefix%", CC.translate(PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Prefixes.DisabledPrefix")))));
        }
        listm.add(CC.translate(""));
        listm.add(CC.CHAT_BAR);
        for (String s : listm) {
            sender.sendMessage(s);
        }
    }
}
