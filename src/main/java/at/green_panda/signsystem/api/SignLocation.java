package at.green_panda.signsystem.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.Objects;
import java.util.UUID;

/**
 * Created by Green_Panda
 * Class create at 27.07.2021 21:31
 */

public class SignLocation {
    UUID worldId;
    int blockX;
    int blockY;
    int blockZ;
    SignFace face;

    public static SignLocation from(Location location, SignFace signFace) {
        return new SignLocation(Objects.requireNonNull(location.getWorld()).getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), signFace);
    }
    public SignLocation(UUID worldId, int blockX, int blockY, int blockZ, SignFace face) {
        this.worldId = worldId;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.face = face;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(worldId), blockX, blockY, blockZ);
    }

    public UUID getWorldId() {
        return worldId;
    }
    public int getBlockX() {
        return blockX;
    }
    public int getBlockY() {
        return blockY;
    }
    public int getBlockZ() {
        return blockZ;
    }
    public SignFace getFace() {
        return face;
    }
}
