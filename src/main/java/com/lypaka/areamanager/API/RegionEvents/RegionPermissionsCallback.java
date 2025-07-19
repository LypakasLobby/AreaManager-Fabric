package com.lypaka.areamanager.API.RegionEvents;

import com.lypaka.areamanager.Regions.Region;
import com.lypaka.areamanager.Regions.RegionPermissions;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface RegionPermissionsCallback {

    /**
     * Event for when region permissions are being checked.
     * Return false to cancel the permission check (i.e., allow bypass).
     */
    Event<RegionPermissionsCallback> EVENT = EventFactory.createArrayBacked(
            RegionPermissionsCallback.class,
            (listeners) -> (player, region, permissions) -> {
                for (RegionPermissionsCallback listener : listeners) {
                    if (!listener.onPermissionCheck(player, region, permissions)) {
                        return false;
                    }
                }
                return true;
            }
    );

    /**
     * Called when a permission check is being run on a region.
     * @param player The player being checked
     * @param region The region in question
     * @param permissions The permissions object
     * @return true to allow the check (enforce permissions), false to bypass
     */
    boolean onPermissionCheck(ServerPlayerEntity player, Region region, RegionPermissions permissions);
}
