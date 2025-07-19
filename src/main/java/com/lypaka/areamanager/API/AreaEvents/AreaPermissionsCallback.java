package com.lypaka.areamanager.API.AreaEvents;

import com.lypaka.areamanager.Areas.Area;
import com.lypaka.areamanager.Areas.AreaPermissions;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface AreaPermissionsCallback {

    Event<AreaPermissionsCallback> EVENT = EventFactory.createArrayBacked(
            AreaPermissionsCallback.class,
            (listeners) -> (player, area, permissions) -> {
                for (AreaPermissionsCallback listener : listeners) {

                    boolean allowed = listener.onPermissionCheck(player, area, permissions);
                    if (!allowed) {

                        // Event is canceled (bypass permission check)
                        return false;

                    }

                }

                return true;
            }

    );

    /**
     * Called when checking permissions for a player entering/leaving an area.
     * Returning false cancels the permission check (i.e. bypass granted).
     */
    boolean onPermissionCheck(ServerPlayerEntity player, Area area, AreaPermissions permissions);

}
