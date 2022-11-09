package services.coral.ability;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import services.coral.ability.commands.ability.AbilityCommand;
import services.coral.ability.commands.partnerpackage.PackageCommand;
import services.coral.ability.commands.partneritems.PartnerItemsCommand;
import services.coral.ability.license.License;
import services.coral.ability.listeners.JoinListener;
import services.coral.ability.listeners.PartnerPackageListener;
import services.coral.ability.managers.AbilityManager;
import services.coral.ability.utils.*;

import services.coral.ability.utils.menu.MenuListener;
import java.util.Arrays;


public class PurgeAbilities extends JavaPlugin {

    @Getter
    private final Boolean globalcooldownB = this.getConfig().getBoolean("PurgeAbilities.Settings.GlobalCooldown.Enabled");
    @Getter
    private final int globalcooldowntime = this.getConfig().getInt("PurgeAbilities.Settings.GlobalCooldown.Cooldown");


    @Getter
    private Cooldowns cooldowns;

    @Getter
    private static PurgeAbilities instance;

    @Getter
    private AbilityManager abilityManager;


    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.reloadConfig();

        //License
        new License(this, getConfig().getString("PurgeAbilities.License", "XXXX"));
        if (!this.isEnabled()) return;

        ppConfig();
        this.cooldowns = new Cooldowns();
        this.abilityManager = new AbilityManager();
        new AbilityCommand(this);
        new PackageCommand(this);
        if (Custom.customEnabled()) {
            new PartnerItemsCommand(this);
            customConfig();
        }
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PartnerPackageListener(), this);
        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);
        getLogger().info("Abilities have been loaded!");
        getLogger().info("The config file has been loaded!");
        Bukkit.getConsoleSender().sendMessage(CC.translate("&6[PurgeAbilities]&e has been &aenabled&e!"));

    }

    public void ppConfig() {
        PartnerPackageConfig.setup();
        PartnerPackageConfig.getConfig().addDefault("PartnerPackage.Item.Name", "&6&lPartner Package");
        PartnerPackageConfig.getConfig().addDefault("PartnerPackage.Item.Lore", Arrays.asList("", "&eRight-Click to receive random items", ""));

        PartnerPackageConfig.getConfig().addDefault("PartnerPackage.Delay", 3);

        PartnerPackageConfig.getConfig().addDefault("PartnerPackage.Rewards.Amount", 3);
        PartnerPackageConfig.getConfig().addDefault("PartnerPackage.Rewards.Command", Arrays.asList("ability give %player% Snowport 3", "ability give %player% SwitchStick 1", "ability give %player% Refill 1", "ability give %player% FreezeGun 1"));

        PartnerPackageConfig.getConfig().addDefault("PartnerPackage.Settings.Sound.Enabled", true);
        PartnerPackageConfig.getConfig().addDefault("PartnerPackage.Settings.Sound.Sound", "BLAZE_HIT");

        PartnerPackageConfig.getConfig().addDefault("PartnerPackage.Settings.Particle.Enabled", true);
        PartnerPackageConfig.getConfig().addDefault("PartnerPackage.Settings.Particle.Particle", "");

        PartnerPackageConfig.getConfig().options().copyDefaults(true);
        PartnerPackageConfig.save();
    }

    public void customConfig() {
        CustomVersionConfig.setup();

        CustomVersionConfig.getConfig().addDefault("CustomVersion.PartnerItemsGUI.Title", "&6Abilties");

        CustomVersionConfig.getConfig().options().copyDefaults(true);
        CustomVersionConfig.save();
    }
}

