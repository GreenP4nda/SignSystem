package at.green_panda.signsystem.api;

import at.green_panda.signsystem.SignSettings;
import at.green_panda.signsystem.SignSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Green_Panda
 * Class create at 27.07.2021 17:48
 */

public class ClickableSign {
    Map<Integer, List<String>> lines;
    Consumer<PlayerInteractEvent> eventConsumer;
    SignLocation location;
    Material type;
    boolean glowing;
    BukkitTask schedulerId;

    public ClickableSign(Consumer<PlayerInteractEvent> eventConsumer, SignLocation location, Material type, boolean glowing) {
        this.lines = new HashMap<>();
        this.eventConsumer = eventConsumer;
        this.location = location;
        this.type = type;
        this.glowing = glowing;
    }

    public ClickableSign addLine(String line) {
        lines.put(lines.size(), Collections.singletonList(line));
        return this;
    }
    public ClickableSign addAnimatedLine(String...line) {
        lines.put(lines.size(), Arrays.asList(line));
        return this;
    }
    public ClickableSign addAnimatedLine(List<String> lines) {
        this.lines.put(lines.size(), lines);
        return this;
    }
    public ClickableSign removeLine(int index) {
        lines.remove(index);
        return this;
    }

    public void spawn() {
        Block blockAt = Objects.requireNonNull(location.toLocation().getWorld()).getBlockAt(location.toLocation());
        blockAt.setType(type);
        Sign sign = (Sign) blockAt.getState();
        sign.setEditable(true);
        WallSign signData = (WallSign) sign.getBlockData();
        signData.setFacing(location.getFace().toBlockFace());
        sign.setGlowingText(glowing);

        final Map<Integer, Integer> lastIndices = new HashMap<>();
        schedulerId = Bukkit.getScheduler().runTaskTimer(SignSystem.getInstance(), ()-> {
            lines.forEach((integer, strings) -> {
                lastIndices.putIfAbsent(integer, 0);
                int i = lastIndices.get(integer) + 1 < strings.size() ? lastIndices.get(integer) : -1;
                i += 1;
                sign.setLine(integer, strings.get(i));
                sign.update();
                lastIndices.put(integer, i);
            });
        }, 0, SignSettings.ticksBetweenUpdate);
    }
    public void remove() {
        stop();
        location.toLocation().getBlock().setType(Material.AIR);
    }
    public void run(PlayerInteractEvent event) {
        eventConsumer.accept(event);
    }
    public void stop() {
        if(schedulerId != null) schedulerId.cancel();
    }

    public List<String> getLineByIndex(int index) {
        return lines.get(index);
    }

    public Map<Integer, List<String>> getLines() {
        return lines;
    }
    public Consumer<PlayerInteractEvent> getEventConsumer() {
        return eventConsumer;
    }
    public SignLocation getLocation() {
        return location;
    }
    public Material getType() {
        return type;
    }
    public boolean isGlowing() {
        return glowing;
    }
    public BukkitTask getSchedulerId() {
        return schedulerId;
    }

    public static class ClickableSignBuilder {
        Consumer<PlayerInteractEvent> eventConsumer;
        SignLocation location;
        Material type;
        boolean glowing;

        public static ClickableSignBuilder of(Consumer<PlayerInteractEvent> eventConsumer, SignLocation location) {
            return new ClickableSignBuilder(eventConsumer, location);
        }

        public ClickableSignBuilder(Consumer<PlayerInteractEvent> eventConsumer, SignLocation location) {
            this.eventConsumer = eventConsumer;
            this.location = location;
        }
        public ClickableSignBuilder setType(Material type) {
            this.type = type;
            return this;
        }
        public ClickableSignBuilder setGlowing(boolean glowing) {
            this.glowing = glowing;
            return this;
        }

        public ClickableSign build() {
            return new ClickableSign(eventConsumer, location, type, glowing);
        }
    }
}


