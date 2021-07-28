package at.green_panda.signsystem;

import at.green_panda.signsystem.api.*;
import at.green_panda.signsystem.commands.SignCommand;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Green_Panda
 * Class create at 27.07.2021 17:46
 */

public class SignSystem extends JavaPlugin {

    public static SignSystem instance;

    public SignManager signManager;
    public SignSettings signSettings;

    public SignCommand signCommand;

    @Override
    public void onEnable() {
        instance = this;

        int pluginId = 12257;
        Metrics metrics = new Metrics(this, pluginId);

        init();
    }

    @Override
    public void onDisable() {
        signManager.saveConfigSigns(null);
    }

    public void init() {
        signManager = new SignManager();
        signSettings = new SignSettings();
        signCommand = new SignCommand();

        saveDefaultConfig();
        SignSettings.ticksBetweenUpdate = getConfig().getInt("ticksBetweenUpdates");
        SignSettings.saveLocation = getConfig().getString("saveLocation");

        Bukkit.getPluginManager().registerEvents(signManager, this);
        getCommand("signsystem").setExecutor(signCommand);
        getCommand("signsystem").setPermission("signsystem.main");

        for (World world : Bukkit.getWorlds()) {
            signManager.loadConfigSigns(world.getUID());
            signManager.prepareConfigSigns();
            signManager.spawnAllSigns(world.getUID());
        }
    }

    public static void sendMessage(Player player, Object...message) {
        String msg = "";
        for (Object o : message) { msg += o; }
        player.sendMessage("§7[§6SignCommands§7] §f" + msg);
    }

    public SignManager getSignManager() {
        return signManager;
    }
    public SignSettings getSignSettings() {
        return signSettings;
    }

    public static SignSystem getInstance() {
        return instance;
    }
}
