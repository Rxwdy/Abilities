package services.coral.ability.utils.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface CommandCompleter {

    List<String> tabComplete(CommandSender sender, String[] args);
}
