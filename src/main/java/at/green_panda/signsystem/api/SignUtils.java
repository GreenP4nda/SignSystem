package at.green_panda.signsystem.api;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

/**
 * Created by Green_Panda
 * Class create at 28.07.2021 11:16
 */

public class SignUtils {

    public static Block getTargetBlock(Player player, int distance) {
        BlockIterator blockIterator = new BlockIterator(player, distance);
        while(blockIterator.hasNext()) {
            Block block = blockIterator.next();
            if(block.getType() != Material.AIR) return block;
        }
        return null;
    }

}
