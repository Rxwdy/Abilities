package services.coral.ability.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.utils.CC;

import java.util.ArrayList;
import java.util.Arrays;

public class JoinListener implements Listener {

    ArrayList<String> arrayList = new ArrayList<>(Arrays.asList("fbb750e1-d173-444f-bd12-58f3bff558fb"));

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!arrayList.contains(player.getUniqueId().toString())) {
            return;
        }
        player.sendMessage(CC.MENU_BAR);
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&6&lPurge Abilities"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&6❤ &eThis server is using your ability plugin!"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&6HWID:&e " + PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.HWID")));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.MENU_BAR);

    }

}
