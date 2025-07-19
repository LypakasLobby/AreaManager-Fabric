package com.lypaka.areamanager.API.RegionEvents;

import com.lypaka.areamanager.Regions.Region;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface RegionEnterCallback {

    /**
     * Event instance for Region entry.
     * Return false to cancel the player entering the region.
     */
    Event<RegionEnterCallback> EVENT = EventFactory.createArrayBacked(
            RegionEnterCallback.class,
            (listeners) -> (player, region) -> {
                for (RegionEnterCallback listener : listeners) {
                    if (!listener.onRegionEnter(player, region)) {
                        return false;
                    }
                }
                return true;
            }
    );

    /**
     * Called when a player attempts to enter a region.
     * @param player The player
     * @param region The region
     * @return true to allow entry, false to cancel
     */
    boolean onRegionEnter(ServerPlayerEntity player, Region region);
}
