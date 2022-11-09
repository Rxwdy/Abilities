package services.coral.ability.utils.command.argument;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import services.coral.ability.utils.command.CommandCompleter;
import services.coral.ability.utils.command.CommandInfo;


public abstract class CommandArgument {

    private CommandInfo commandInfo;
    private CommandCompleter commandCompleter;

    public abstract void execute(CommandSender sender, Command command, String label, String[] args);

    public CommandArgument() {
        if (this.getClass().isAnnotationPresent(CommandInfo.class)) {
            this.commandInfo = this.getClass().getAnnotation(CommandInfo.class);
            if (this instanceof CommandCompleter) {
                this.commandCompleter = (CommandCompleter) this;
            }
        }
    }

    public CommandInfo getCommandInfo() {
        return this.commandInfo;
    }

    public CommandCompleter getCommandCompleter() {
        return this.commandCompleter;
    }
}
