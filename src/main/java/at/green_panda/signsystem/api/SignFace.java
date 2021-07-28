package at.green_panda.signsystem.api;

import org.bukkit.block.BlockFace;

/**
 * Created by Green_Panda
 * Class create at 27.07.2021 21:32
 */

public enum SignFace {
    NORTH(BlockFace.NORTH),
    EAST(BlockFace.EAST),
    SOUTH(BlockFace.SOUTH),
    WEST(BlockFace.WEST);

    BlockFace face;
    SignFace(BlockFace face) {
        this.face = face;
    }

    public BlockFace toBlockFace() {
        return this.face;
    }
    public static SignFace fromBlockFace(BlockFace blockFace) {
        switch (blockFace) {
            case SOUTH:
                return SignFace.SOUTH;
            case EAST:
                return SignFace.EAST;
            case WEST:
                return SignFace.WEST;
            default:
                return SignFace.NORTH;
        }
    }
}
