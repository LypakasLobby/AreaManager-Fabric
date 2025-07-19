package com.lypaka.areamanager.API;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Called when AreaManager finishes loading all areas.
 * Used by other mods that depend on AreaManager to ensure safe loading order.
 */
@FunctionalInterface
public interface FinishedLoadingCallback {

    Event<FinishedLoadingCallback> EVENT = EventFactory.createArrayBacked(
            FinishedLoadingCallback.class,
            (listeners) -> () -> {
                for (FinishedLoadingCallback listener : listeners) {
                    listener.onFinishedLoading();
                }
            }
    );

    /**
     * Called when AreaManager finishes loading everything.
     */
    void onFinishedLoading();
}
