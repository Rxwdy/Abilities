package services.coral.ability.items.pocketbard;

import javafx.scene.input.InputMethodTextRun;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.utils.CC;
import services.coral.ability.utils.menu.Button;
import services.coral.ability.utils.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class PocketBardMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return CC.translate(PurgeAbilities.getInstance().getConfig().getString("PocketBard.Menu.Title"));
    }

    public int getSize() {
        return 27;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        ItemStack strength = new ItemStack(Material.BLAZE_POWDER, 1);
        ItemMeta strengthMeta = strength.getItemMeta();
        strengthMeta.setLore(CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PocketBard.Menu.Items.Strength.Lore")));
        strengthMeta.setDisplayName(CC.translate(PurgeAbilities.getInstance().getConfig().getString("PocketBard.Menu.Items.Strength.Name")));
        strength.setItemMeta(strengthMeta);

        ItemStack resistance = new ItemStack(Material.IRON_INGOT, 1);
        ItemMeta resistanceMeta = resistance.getItemMeta();
        resistanceMeta.setLore(CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PocketBard.Menu.Items.Resistance.Lore")));
        resistanceMeta.setDisplayName(CC.translate(PurgeAbilities.getInstance().getConfig().getString("PocketBard.Menu.Items.Resistance.Name")));
        resistance.setItemMeta(resistanceMeta);

        ItemStack regen = new ItemStack(Material.GHAST_TEAR, 1);
        ItemMeta regenMeta = regen.getItemMeta();
        regenMeta.setLore(CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PocketBard.Menu.Items.Regeneration.Lore")));
        regenMeta.setDisplayName(CC.translate(PurgeAbilities.getInstance().getConfig().getString("PocketBard.Menu.Items.Regeneration.Name")));
        regen.setItemMeta(regenMeta);

        ItemStack jump = new ItemStack(Material.FEATHER, 1);
        ItemMeta jumpMeta = jump.getItemMeta();
        jumpMeta.setLore(CC.translate(PurgeAbilities.getInstance().getConfig().getStringList("PocketBard.Menu.Items.JumpBoost.Lore")));
        jumpMeta.setDisplayName(CC.translate(PurgeAbilities.getInstance().getConfig().getString("PocketBard.Menu.Items.JumpBoost.Name")));
        jump.setItemMeta(jumpMeta);

        HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return strength;
            }

            public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                player.getInventory().addItem(strength);

                if (player.getInventory().getItemInHand().getAmount() == 1) {
                    player.getInventory().setItemInHand(null);
                    player.updateInventory();
                    return;
                }
                player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                player.closeInventory();
                player.updateInventory();
            }
        });

        buttons.put(12, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return resistance;
            }

            public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                player.getInventory().addItem(resistance);
                if (player.getInventory().getItemInHand().getAmount() == 1) {
                    player.getInventory().setItemInHand(null);
                    player.updateInventory();
                    return;
                }
                player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                player.closeInventory();
                player.updateInventory();
            }
        });

        buttons.put(14, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return regen;
            }

            public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                player.getInventory().addItem(regen);
                if (player.getInventory().getItemInHand().getAmount() == 1) {
                    player.getInventory().setItemInHand(null);
                    player.updateInventory();
                    return;
                }
                player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                player.closeInventory();
                player.updateInventory();
            }
        });

        buttons.put(16, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return jump;
            }

            public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                player.getInventory().addItem(jump);
                if (player.getInventory().getItemInHand().getAmount() == 1) {
                    player.getInventory().setItemInHand(null);
                    player.updateInventory();
                    return;
                }
                player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                player.closeInventory();
                player.updateInventory();

            }
        });

        return buttons;
    }
}
