package at.green_panda.signsystem.config;

import at.green_panda.signsystem.api.SignFace;
import at.green_panda.signsystem.api.SignLocation;
import at.green_panda.signsystem.config.ConfigFunction;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Green_Panda
 * Class create at 28.07.2021 09:40
 */

public class ConfigSign {
    Map<Integer, List<String>> lines;
    List<ConfigFunction> functions;

    String worldId;
    int blockX;
    int blockY;
    int blockZ;
    String face;

    String type;
    boolean glowing;

    public ConfigSign(Map<Integer, List<String>> lines, List<ConfigFunction> functions, SignLocation signLocation, Material type, boolean glowing) {
        this.lines = lines;
        this.functions = functions;
        this.worldId = signLocation.getWorldId().toString();
        this.blockX = signLocation.getBlockX();
        this.blockY = signLocation.getBlockY();
        this.blockZ = signLocation.getBlockZ();
        this.face = signLocation.getFace().toString();

        this.type = type.toString();
        this.glowing = glowing;
    }

    public Map<Integer, List<String>> getLines() {
        return lines;
    }
    public List<ConfigFunction> getFunctions() {
        return functions;
    }
    public SignLocation getLocation() {
        return new SignLocation(UUID.fromString(worldId), blockX, blockY, blockZ, SignFace.valueOf(face));
    }
    public String getType() {
        return type;
    }
    public boolean isGlowing() {
        return glowing;
    }
}
