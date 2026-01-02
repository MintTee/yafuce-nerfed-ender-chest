package yafuce_nerfed_ender_chest;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yafuce_nerfed_ender_chest.config.ConfigIO;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks per-player "free access" periods for Ender Chests.
 *
 * - Free access = chest can be opened with any item.
 * - After free access expires, the opener item must be consumed for next opening (eye of ender by default).
 */
public class EnderChestAccessManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("YafuceECAccess");
    private static final Map<UUID, Integer> freeAccessRemaining = new HashMap<>();

    /** Starts or refreshes free access period for a player */
    public static void startFreeAccess(ServerPlayerEntity player) {
        freeAccessRemaining.put(player.getUuid(), ConfigIO.CURRENT.timeTickFreeAccess);
        LOGGER.info("Starting free access for {} ({} ticks)", player.getName().getString(), ConfigIO.CURRENT.timeTickFreeAccess);
    }

    /** Returns true if the player currently has free access */
    public static boolean hasFreeAccess(ServerPlayerEntity player) {
        boolean free = freeAccessRemaining.containsKey(player.getUuid());
        LOGGER.info("Checking free access for {}: {}", player.getName().getString(), free);
        return free;
    }

    /** Called every server tick to decrement free access timers */
    public static void tick() {
        Iterator<Map.Entry<UUID, Integer>> iterator = freeAccessRemaining.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                LOGGER.info("Free access expired for UUID {}", entry.getKey());
                iterator.remove();
            } else {
                entry.setValue(remaining);
            }
        }
    }

    /** Register the server tick event */
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> tick());
        LOGGER.info("EnderChestAccessManager registered");
    }
}
