package services.coral.ability.commands.partneritems;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.managers.AbilityProvider;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.CustomVersionConfig;
import services.coral.ability.utils.ItemBuilder;
import services.coral.ability.utils.menu.Button;
import services.coral.ability.utils.menu.Menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PartnerItemsMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return CC.translate("&6Abilities");
    }
    int a = -1;
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (AbilityProvider ability : PurgeAbilities.getInstance().getAbilityManager().getAbilities()) {
            boolean b = ability.isEnabled();
            if (!b) {
                continue;
            }
            a++;
            buttons.put(a, new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(ability.getItemStack()).lore(ability.getLore()).name(ability.getDisplayName()).build();
                }
            });
        }

        return buttons;
    }
}
