package services.coral.ability.commands.ability;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.ItemBuilder;
import services.coral.ability.utils.menu.Button;
import services.coral.ability.utils.menu.Menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AbilityListMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&6Abilities");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int a = -1;
        for (AbilityProvider ability : PurgeAbilities.getInstance().getAbilityManager().getAbilities()) {
            Boolean b = ability.isEnabled();
            a++;
            buttons.put(a, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(ability.getItemStack()).lore(CC.translate(Arrays.asList("", "&6Right-Click to view Ability Info", "&6Left-Click to toggle Ability", "", b ? "&6Status: &aEnabled" : "&6Status: &cDisabled"))).name(ability.getDisplayName()).build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                    if (clickType.isRightClick()) {
                        Bukkit.dispatchCommand(player, "ability info " + ability.getName());
                        player.closeInventory();
                    }
                    if (clickType.isLeftClick()) {
                        Bukkit.dispatchCommand(player, "ability toggle " + ability.getName());
                        player.closeInventory();
                        openMenu(player);
                    }
                }
            });
        }

        return buttons;
    }
}
