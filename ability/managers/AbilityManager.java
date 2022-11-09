package services.coral.ability.managers;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.reflections.Reflections;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.items.*;
import services.coral.ability.items.pocketbard.PocketBard;
import services.coral.ability.utils.Custom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AbilityManager {

    @Getter
    private final List<AbilityProvider> abilities = new ArrayList<>();

    public AbilityManager() throws InstantiationException, IllegalAccessException, IOException {
        registerItems();
    }

    public void registerItems() throws IllegalAccessException, InstantiationException, IOException {
        Reflections reflections = new Reflections("services.[purge].ability.items");
        Set<Class<? extends AbilityProvider>> classes = reflections.getSubTypesOf(AbilityProvider.class);
        File file = new File(PurgeAbilities.getInstance().getDataFolder(), "config.yml");
        for (Class<? extends AbilityProvider> clazz : classes) {
            AbilityProvider abilityProvider = clazz.newInstance();
            if (abilityProvider.isCustom()) {
                if (Custom.customEnabled()) {
                    this.abilities.add(abilityProvider);
                }
                continue;
            }
            if (file.exists()) {
                if (PurgeAbilities.getInstance().getConfig().get("PocketBard.Item.Material") == null) {
                    continue;
                }
                PurgeAbilities.getInstance().getConfig().set("PocketBard.Item.Material.Material", "INK_SACK");
                PurgeAbilities.getInstance().getConfig().set("PocketBard.Item.Material.Byte", 1);
                PurgeAbilities.getInstance().getConfig().save(file);
            }
            this.abilities.add(abilityProvider);
            Bukkit.getPluginManager().registerEvents(abilityProvider, PurgeAbilities.getInstance());
        }



    }

    public AbilityProvider getByName(String name) {
        return this.abilities.stream().filter(abilityProvider -> abilityProvider.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
