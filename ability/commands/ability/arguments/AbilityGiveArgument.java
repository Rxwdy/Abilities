package services.coral.ability.commands.ability.arguments;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.command.CommandCompleter;
import services.coral.ability.utils.command.CommandInfo;
import services.coral.ability.utils.command.argument.CommandArgument;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        names = {"give"},
        permission = "purge.abilities.give",
        description = "Gives an ability to a player",
        usage = "give <player> <ability> <amount>"
)
public class AbilityGiveArgument extends CommandArgument implements CommandCompleter {

    private final List<String> givemessage = CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PurgeAbilities.Messages.GiveMessage"));

    private PurgeAbilities instance;

    public AbilityGiveArgument(PurgeAbilities instance) {
        this.instance = instance;
    }

    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 4) {
            sender.sendMessage(CC.createUsage(label, "give <player> [ability] [amount]"));
            return;
        }
        AbilityProvider abilityProvider = PurgeAbilities.getInstance().getAbilityManager().getByName(args[2]);
        if (abilityProvider == null) {
            sender.sendMessage(CC.translate("&cThat ability does not exist."));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(CC.translate("&cThat player is not online."));
        }
        try {
            ItemStack itemStack = new ItemStack(abilityProvider.getItemStack());
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> lore = abilityProvider.getLore();
            List<String> rlore = new ArrayList<String>();
            for (String s : lore) {
                s.replace("%id%", "#" + RandomStringUtils.random(10));
                rlore.add(s);
            }
            itemMeta.setLore(CC.translate(abilityProvider.getLore()));
            itemStack.setAmount(Integer.parseInt(args[3]));
            for (String key : PurgeAbilities.getInstance().getConfig().getConfigurationSection(abilityProvider.getName() + ".Enchantments").getKeys(false)) {
                ConfigurationSection section = PurgeAbilities.getInstance().getConfig().getConfigurationSection(abilityProvider.getName() + ".Enchantments." + key);
                itemMeta.addEnchant(Enchantment.getByName(section.getString("Enchantment")), section.getInt("Level"), false);
            }
            itemMeta.setDisplayName(CC.translate(abilityProvider.getDisplayName()));
            itemStack.setItemMeta(itemMeta);
            for (ItemStack value : target.getInventory().addItem(itemStack).values()) {
                target.getWorld().dropItemNaturally(target.getLocation(), value);
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(CC.translate("&cInvalid Amount"));
        }
        for (String s : givemessage) {
            sender.sendMessage(s.replace("%ability%", abilityProvider.getDisplayName()));
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
