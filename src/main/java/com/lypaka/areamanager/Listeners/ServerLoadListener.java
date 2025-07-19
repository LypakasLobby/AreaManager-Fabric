package com.lypaka.areamanager.Listeners;

import com.lypaka.areamanager.AreaManager;
import com.lypaka.areamanager.Regions.RegionHandler;
import com.lypaka.lypakautils.API.PlayerLandMovementCallback;
import com.lypaka.lypakautils.API.PlayerWaterMovementCallback;
import com.lypaka.lypakautils.ConfigGetters;
import com.lypaka.shadow.configurate.objectmapping.ObjectMappingException;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;

public class ServerLoadListener implements ServerLifecycleEvents.ServerStarted {

    @Override
    public void onServerStarted (MinecraftServer server) {

        if (!ConfigGetters.tickListenerEnabled) {

            AreaManager.logger.warn("============ WARNING ============");
            AreaManager.logger.warn("Detected the tick listener in LypakaUtils is not enabled!");
            AreaManager.logger.warn("This configuration node is required to be enabled in order for AreaManager to work!");
            AreaManager.logger.warn("Please go into your lypakautils.conf file and set the tick listener to true to enable it, and then restart the server.");

        }
        PlayerLandMovementCallback.EVENT.register(new MovementListener());
        PlayerWaterMovementCallback.EVENT.register(new SwimListener());
        UseBlockCallback.EVENT.register(new WandSecondaryInteractListener());
        AttackBlockCallback.EVENT.register(new WandPrimaryInteractListener());
        ServerPlayConnectionEvents.JOIN.register(new ConnectionListener());

        try {

            RegionHandler.loadRegions();

        } catch (IOException | ObjectMappingException e) {

            throw new RuntimeException(e);

        }

    }

}
