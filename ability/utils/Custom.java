package services.coral.ability.utils;

import services.coral.ability.PurgeAbilities;

public class Custom {

    public static boolean customEnabled() {
        return PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.Custom").equals("NicksHCFCore");
    }

}
