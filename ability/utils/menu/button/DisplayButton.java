package services.coral.ability.utils.menu.button;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import services.coral.ability.utils.menu.Button;

@AllArgsConstructor
@Getter
@Setter
public class DisplayButton extends Button {

	private ItemStack itemStack;
	private boolean cancel;

	@Override
	public ItemStack getButtonItem(Player player) {
		if (this.itemStack == null) {
			return new ItemStack(Material.AIR);
		} else {
			return this.itemStack;
		}
	}

	@Override
	public boolean shouldCancel(Player player, ClickType clickType) {
		return this.cancel;
	}

}
