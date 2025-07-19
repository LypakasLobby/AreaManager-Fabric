package com.lypaka.areamanager.API.AreaEvents;

import com.lypaka.areamanager.Areas.Area;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public interface AreaEnterCallback {

    boolean onPlayerAreaEnter (ServerPlayerEntity player, Area area, boolean canEnter);

    Event<AreaEnterCallback> EVENT = EventFactory.createArrayBacked(
            AreaEnterCallback.class,
            (listeners) -> (player, area, canEnter) -> {

                for (AreaEnterCallback listener : listeners) {

                    canEnter = listener.onPlayerAreaEnter(player, area, canEnter);
                    if (!canEnter) break;
                    //listener.onPlayerAreaEnter(player, area, canEnter);

                }

                return canEnter;

            }

    );

}
