package at.green_panda.signsystem.commands;

import at.green_panda.signsystem.SignSystem;
import at.green_panda.signsystem.api.SignFace;
import at.green_panda.signsystem.api.SignLocation;
import at.green_panda.signsystem.api.SignUtils;
import at.green_panda.signsystem.config.ConfigFunction;
import at.green_panda.signsystem.config.ConfigSign;
import at.green_panda.signsystem.config.SignFunctionType;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Green_Panda
 * Class create at 28.07.2021 13:28
 */

public class SignCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) return false;
        Player sender = ((Player) commandSender);
        if(strings.length > 0) {
            switch (strings[0]) {
                case "create":
                    {
                        if(!sender.hasPermission("signsystem.create")) {
                            SignSystem.sendMessage(sender, "Unable to run command!");
                            return false;
                        }
                        Block lookingAt = SignUtils.getTargetBlock(sender, 5);
                        if(lookingAt == null || !lookingAt.getType().name().contains("WALL_SIGN")) {
                            SignSystem.sendMessage(sender, "§cPlease target a sign!");
                            return false;
                        }
                        Map<Integer, List<String>> lines = new HashMap<>();
                        lines.put(0, Arrays.asList("§1default", "§2default"));

                        List<ConfigFunction> functions = new ArrayList<>();
                        functions.add(new ConfigFunction(SignFunctionType.PLAYER_COMMAND.toString(), "say default"));

                        SignFace signFace = SignFace.fromBlockFace(((WallSign) lookingAt.getBlockData()).getFacing());
                        SignLocation signLocation = SignLocation.from(lookingAt.getLocation(), signFace);

                        ConfigSign configSign = new ConfigSign(lines, functions, signLocation, lookingAt.getType(), false);
                        SignSystem.getInstance().getSignManager().registerConfigSign(configSign);
                        SignSystem.sendMessage(sender, "§aYour Sign has been created!");
                        return false;
                    }
                case "save":
                    {
                        if(!sender.hasPermission("signsystem.save")) {
                            SignSystem.sendMessage(sender, "Unable to run command!");
                            return false;
                        }
                        UUID worldId = null;
                        if(strings.length > 1 && strings[1].equals("current")) {
                            worldId = ((Player) commandSender).getWorld().getUID();
                        }
                        SignSystem.getInstance().getSignManager().saveConfigSigns(worldId);
                        SignSystem.sendMessage(sender, "§aSaved Signs!");
                        return false;
                    }
                case "update":
                    {
                        if(!sender.hasPermission("signsystem.update")) {
                            SignSystem.sendMessage(sender, "Unable to run command!");
                            return false;
                        }
                        SignSystem.getInstance().getSignManager().stopAllSigns(((Player)commandSender).getWorld().getUID());
                        SignSystem.getInstance().getSignManager().loadConfigSigns(((Player)commandSender).getWorld().getUID());
                        SignSystem.getInstance().getSignManager().prepareConfigSigns();
                        SignSystem.getInstance().getSignManager().getClickableSigns().forEach((uuid, clickableSign) -> {
                            clickableSign.spawn();
                            System.out.println(clickableSign.getSchedulerId());
                        });
                        return false;
                    }
                default:
                    sender.performCommand("signsystem");
                    break;
            }
        } else {
            SignSystem.sendMessage(sender, "§f------------ §7[ §6SignCommands §7] §f------------");
            SignSystem.sendMessage(sender, " §7- §6create §7: §fThe sign you are looking at will be added to the config. (Once the signs has been saved)");
            SignSystem.sendMessage(sender, " §7- §6save §7: §fAll created signs will be saved in their respective configs.");
            SignSystem.sendMessage(sender, " §7- §6update §7: §fReloads all config files.");
            SignSystem.sendMessage(sender, "§f------------ §7[ §6SignCommands §7] §f------------");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> tabCompletions = new ArrayList<>();
        switch (strings.length) {
            case 1:
                if(commandSender.hasPermission("signsystem.create")) tabCompletions.add("create");
                if(commandSender.hasPermission("signsystem.save")) tabCompletions.add("save");
                if(commandSender.hasPermission("signsystem.update")) tabCompletions.add("update");
                break;
            case 2:
                switch(strings[1]) {
                    case "save":
                        if(commandSender.hasPermission("signsystem.save.current")) tabCompletions.add("current");
                        if(commandSender.hasPermission("signsystem.save.all")) tabCompletions.add("all");
                        break;
                }
                break;
        }
        return tabCompletions;
    }
}
