package com.lypaka.areamanager.API.AreaEvents;

import com.lypaka.areamanager.Areas.Area;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface AreaLeaveCallback {

    boolean onPlayerAreaLeave (ServerPlayerEntity player, Area area, boolean canLeave);

    Event<AreaLeaveCallback> EVENT = EventFactory.createArrayBacked(
            AreaLeaveCallback.class,
            (listeners) -> (player, area, canLeave) -> {

                for (AreaLeaveCallback listener : listeners) {

                    canLeave = listener.onPlayerAreaLeave(player, area, canLeave);
                    if (!canLeave) break;

                }

                return canLeave;

            }

    );

}
