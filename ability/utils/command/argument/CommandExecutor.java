package services.coral.ability.utils.command.argument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.command.CommandCompleter;
import services.coral.ability.utils.command.CommandInfo;
import services.coral.ability.utils.command.CommandWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class CommandExecutor extends CommandWrapper implements CommandCompleter {

    private final List<CommandArgument> arguments = new ArrayList<>();

    public abstract boolean executeOther(CommandSender sender, Command command, String label, String[] args);

    public CommandExecutor(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }

        for (CommandArgument argument : this.arguments) {
            if (Arrays.asList(argument.getCommandInfo().names()).contains(args[0].toLowerCase())) {

                if (!canUse(sender, argument.getCommandInfo())) {
                    sender.sendMessage(CC.RED + "You don't have permissions to execute this command");
                    return;
                }

                argument.execute(sender, command, label, args);
                return;
            }
        }

        this.sendUsage(sender);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> toReturn = new ArrayList<>();
            arguments.forEach(argument -> {
                if (canUse(sender, argument.getCommandInfo())) {
                    toReturn.add(argument.getCommandInfo().names()[0]);
                }
            });
            return toReturn;
        }

        for (CommandArgument argument : arguments) {
            if (Arrays.asList(argument.getCommandInfo().names()).contains(args[0])) {
                if (!canUse(sender, argument.getCommandInfo())) {
                    continue;
                }

                if (argument.getCommandCompleter() != null) {
                    return argument.getCommandCompleter().tabComplete(sender, args);
                }
            }
        }
        return null;
    }

    public List<CommandArgument> getArguments() {
        return this.arguments;
    }

    public void addArgument(CommandArgument argument) {
        this.arguments.add(argument);
    }

    public boolean canUse(CommandSender sender, CommandInfo commandInfo) {
        switch (commandInfo.permission()) {
            case "":
                return true;
            case "op":
                return sender.isOp();
            case "console":
                return sender instanceof ConsoleCommandSender;
            case "player":
                return sender instanceof Player;
            default:
                if (commandInfo.playerOnly()) {
                    return (sender instanceof Player) && (sender.hasPermission(commandInfo.permission()));
                }
                return sender.hasPermission(commandInfo.permission());
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(CC.CHAT_BAR);
        sender.sendMessage(CC.GOLD + CC.BOLD + this.getCommandInfo().helpTitle() + " Help ");
        for (CommandArgument argument : arguments) {
            CommandInfo commandInfo = argument.getCommandInfo();
            if (this.canUse(sender, commandInfo)) {
                sender.sendMessage(CC.YELLOW + " /" + this.getCommandInfo().names()[0] + " "
                        + commandInfo.usage() + CC.GOLD + " » " + CC.WHITE + commandInfo.description());
            }
        }
        sender.sendMessage(CC.CHAT_BAR);
    }
}
