package com.lypaka.areamanager.API.AreaEvents;

import com.lypaka.areamanager.Areas.Area;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Called when a player is detected to be swimming in an area
 */
public class AreaSwimCallback {

    @FunctionalInterface
    public interface Teleport {
        /**
         * Called when a player is swimming in a teleport-enabled area.
         * Return false to cancel the teleport.
         */
        boolean onSwim(ServerPlayerEntity player, Area area);
    }

    @FunctionalInterface
    public interface Kill {
        /**
         * Called when a player is swimming in a kill-enabled area.
         * Return false to cancel the death.
         */
        boolean onSwim(ServerPlayerEntity player, Area area);
    }

    public static final Event<Teleport> TELEPORT_EVENT = EventFactory.createArrayBacked(
            Teleport.class,
            (listeners) -> (player, area) -> {
                for (Teleport listener : listeners) {
                    if (!listener.onSwim(player, area)) {
                        return false; // Cancel teleport
                    }
                }
                return true; // Allow teleport
            }
    );

    public static final Event<Kill> KILL_EVENT = EventFactory.createArrayBacked(
            Kill.class,
            (listeners) -> (player, area) -> {
                for (Kill listener : listeners) {
                    if (!listener.onSwim(player, area)) {
                        return false; // Cancel kill
                    }
                }
                return true; // Allow kill
            }
    );

}
