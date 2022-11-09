package services.coral.ability.commands.ability.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.command.CommandCompleter;
import services.coral.ability.utils.command.CommandInfo;
import services.coral.ability.utils.command.argument.CommandArgument;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        names = {"resetcooldown"},
        permission = "purge.abilities.resetcooldown",
        description = "Toggles an ability item on or off",
        usage = "resetcooldown <player> <ability>"
)
public class AbilityResetCooldownArgument extends CommandArgument implements CommandCompleter {

    private final List<String> invalidabilitymessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.InvalidAbility"));

    private final List<String> targetcooldownreset = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ResetCooldown.TargetPlayer"));
    private final List<String> playercooldownreset = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.ResetCooldown.Player"));

    private final List<String> invalidplayermessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.InvalidPlayer"));

    private PurgeAbilities instance;

    public AbilityResetCooldownArgument(PurgeAbilities instance) {
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(CC.createUsage(label, "resetcooldown <player> <ability>"));
            return;
        }
        AbilityProvider abilityProvider = PurgeAbilities.getInstance().getAbilityManager().getByName(args[2]);
        if (abilityProvider == null) {
            for (String s : invalidabilitymessage) {
                sender.sendMessage(s);
            }
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            for (String s : invalidplayermessage) {
                sender.sendMessage(s);
            }
            return;
        }
        for (String s : targetcooldownreset) {
            target.sendMessage(s.replace("%ability%", abilityProvider.getName()).replace("%player%", sender instanceof Player ? sender.getName() : CC.translate("&cCONSOLE")));
        }
        for (String s : playercooldownreset) {
            sender.sendMessage(s.replace("%ability%", abilityProvider.getName()).replace("%player%", target.getName()));
        }
        removeCooldown(abilityProvider, target);

    }

    private void removeCooldown(AbilityProvider abilityname, Player player) {
        String name = abilityname.getName().toUpperCase();
        switch (name) {
            case "SNOWPORT":
                PurgeAbilities.getInstance().getCooldowns().getSnowport().removeCooldown(player);
                break;
            case "COCAINE":
                PurgeAbilities.getInstance().getCooldowns().getCocaine().removeCooldown(player);
                break;
            case "SWITCHSTICK":
                PurgeAbilities.getInstance().getCooldowns().getSwitchstick().removeCooldown(player);
                break;
            case "REFILL":
                PurgeAbilities.getInstance().getCooldowns().getRefill().removeCooldown(player);
                break;
            case "BELCHBOMB":
                PurgeAbilities.getInstance().getCooldowns().getBelchbomb().removeCooldown(player);
                break;
            case "INSTANTCRAPPLE":
                PurgeAbilities.getInstance().getCooldowns().getInstantcrapple().removeCooldown(player);
                break;
            case "FREEZEGUN":
                PurgeAbilities.getInstance().getCooldowns().getFreezegun().removeCooldown(player);
                break;
            case "POCKETBARD":
                PurgeAbilities.getInstance().getCooldowns().getPocketbard().removeCooldown(player);
                break;
            case "WEED":
                PurgeAbilities.getInstance().getCooldowns().getWeed().removeCooldown(player);
                break;
            case "EXOTICBONE":
                PurgeAbilities.getInstance().getCooldowns().getExoticbone().removeCooldown(player);
                break;
            case "TIMERBONE":
                PurgeAbilities.getInstance().getCooldowns().getTimerbone().removeCooldown(player);
                break;
            case "ROCKET":
                PurgeAbilities.getInstance().getCooldowns().getRocket().removeCooldown(player);
                break;
            case "SWAPPERAXE":
                PurgeAbilities.getInstance().getCooldowns().getSwapperaxe().removeCooldown(player);
                break;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> abilities = new ArrayList<>();
        if (args.length == 3) {
            for (AbilityProvider ability : PurgeAbilities.getInstance().getAbilityManager().getAbilities()) {
                abilities.add(ability.getName());
            }
        }
        return abilities;
    }
}
