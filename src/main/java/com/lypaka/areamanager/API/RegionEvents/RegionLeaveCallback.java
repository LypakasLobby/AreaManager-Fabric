package com.lypaka.areamanager.API.RegionEvents;

import com.lypaka.areamanager.Regions.Region;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface RegionLeaveCallback {

    /**
     * Event instance for leaving a region.
     * Return false to cancel the leave.
     */
    Event<RegionLeaveCallback> EVENT = EventFactory.createArrayBacked(
            RegionLeaveCallback.class,
            (listeners) -> (player, region) -> {
                for (RegionLeaveCallback listener : listeners) {
                    if (!listener.onRegionLeave(player, region)) {
                        return false;
                    }
                }
                return true;
            }
    );

    /**
     * Called when a player tries to leave a region.
     * @param player The player
     * @param region The region being left
     * @return true to allow leaving, false to cancel
     */
    boolean onRegionLeave(ServerPlayerEntity player, Region region);
}
