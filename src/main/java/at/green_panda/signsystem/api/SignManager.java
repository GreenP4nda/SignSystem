package at.green_panda.signsystem.api;

import at.green_panda.signsystem.*;
import at.green_panda.signsystem.api.ClickableSign.ClickableSignBuilder;
import at.green_panda.signsystem.config.ConfigSignSave;
import at.green_panda.signsystem.config.ConfigFunction;
import at.green_panda.signsystem.config.ConfigSign;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Green_Panda
 * Class create at 27.07.2021 17:47
 */

public class SignManager implements Listener {
    Map<UUID, ClickableSign> clickableSigns = new HashMap<>();
    List<ConfigSign> configSigns = new ArrayList<>();

    public void registerSign(ClickableSign clickableSign) {
        clickableSigns.put(UUID.randomUUID(), clickableSign);
    }
    public void registerConfigSign(ConfigSign configSign) {
        configSigns.add(configSign);
    }

    public void loadConfigSigns(UUID worldId) {
        Gson gson = new Gson();
        ConfigSignSave loaded = new ConfigSignSave(new ArrayList<>());
        try { loaded = gson.fromJson(new JsonReader(new FileReader(SignSettings.saveLocation + worldId.toString() + ".json")), ConfigSignSave.class);
        } catch (FileNotFoundException ignored) {
            System.out.println("[SignSystem] Couldn't find save File for " + worldId + "!");
        }
        configSigns = loaded.getClickableSigns();
    }
    public void saveConfigSigns(UUID worldId) {
        List<UUID> targetWorlds = new ArrayList<>();
        if(worldId == null) {
            for (World world : Bukkit.getWorlds()) {
                targetWorlds.add(world.getUID());
            }
        }

        Map<UUID, List<ConfigSign>> groups = new HashMap<>();
        for (ConfigSign configSign : configSigns) {
            if(!targetWorlds.contains(configSign.getLocation().getWorldId())) continue;
            groups.putIfAbsent(configSign.getLocation().getWorldId(), new ArrayList<>(Collections.singletonList(configSign)));
            groups.get(configSign.getLocation().getWorldId()).add(configSign);
        }

        groups.forEach((uuid, configSigns) -> {
            File file = new File(SignSettings.saveLocation + uuid.toString() + ".json");
            if(file.exists()) {
                file.delete();
            } else {
                file = new File(SignSettings.saveLocation);
                file.mkdirs();
            }
            Gson gson = new Gson();
            FileWriter fileWriter = null;
            try { fileWriter = new FileWriter(SignSettings.saveLocation + uuid.toString() + ".json");
            } catch (IOException e) { e.printStackTrace(); }

            gson.toJson(new ConfigSignSave(configSigns), fileWriter);
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) { e.printStackTrace(); }
        });
    }
    public void prepareConfigSigns() {
        for (ConfigSign configSign : configSigns) {
            ClickableSign clickableSign = ClickableSignBuilder.of((playerInteractEvent)-> {
                for (ConfigFunction function : configSign.getFunctions()) {
                    switch (function.getSignFunctionType()) {
                        case "PLAYER_COMMAND":
                            playerInteractEvent.getPlayer().performCommand(function.getFunction());
                            break;
                        case "CONSOLE_COMMAND":
                            String command = function.getFunction();
                            command = command.replace("%player%", playerInteractEvent.getPlayer().getName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                            break;
                    }
                }
            }, configSign.getLocation())
                    .setType(Material.getMaterial(configSign.getType()))
                    .setGlowing(configSign.isGlowing())
                    .build();
            configSign.getLines().forEach((integer, strings) -> {
                clickableSign.getLines().put(integer, strings);
            });
            registerSign(clickableSign);
        }
    }
    public void spawnAllSigns(UUID worldID) {
        List<ClickableSign> signsByWorld = getSignsByWorld(worldID);
        signsByWorld.forEach(ClickableSign::spawn);
    }
    public void stopAllSigns(UUID worldId) {
        List<ClickableSign> signsByBlock = getSignsByWorld(worldId);
        signsByBlock.forEach(ClickableSign::remove);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        System.out.println("[SignPlugin] Registered new World. Name: '" + event.getWorld().getName() + "'");

        World loadedWorld = event.getWorld();

        loadConfigSigns(loadedWorld.getUID());
        prepareConfigSigns();
        spawnAllSigns(event.getWorld().getUID());

        System.out.println("[SignPlugin] ClickableSign setup finished.");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ClickableSign clickableSign = getSignsByBlock(event.getClickedBlock().getLocation());
        if(clickableSign != null) clickableSign.run(event);
    }

    public List<ClickableSign> getSignsByWorld(UUID worldId) {
        List<ClickableSign> back = new ArrayList<>();
        clickableSigns.forEach((uuid, clickableSign) -> {
            if(clickableSign.getLocation().getWorldId().equals(worldId)) back.add(clickableSign);
        });
        return back;
    }
    public ClickableSign getSignsByBlock(Location block) {
        AtomicReference<ClickableSign> back = new AtomicReference<>();
        clickableSigns.forEach((uuid, clickableSign) -> {
            if(clickableSign.getLocation().toLocation().equals(block)) {
                back.set(clickableSign);
            }
        });
        return back.get();
    }

    public Map<UUID, ClickableSign> getClickableSigns() {
        return clickableSigns;
    }
}
