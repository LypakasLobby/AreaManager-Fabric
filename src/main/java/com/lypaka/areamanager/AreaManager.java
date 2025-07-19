package com.lypaka.areamanager;

import com.lypaka.areamanager.API.AreaEvents.AreaLeaveCallback;
import com.lypaka.areamanager.API.AreaEvents.AreaPermissionsCallback;
import com.lypaka.areamanager.API.AreaEvents.AreaSwimCallback;
import com.lypaka.areamanager.API.FinishedLoadingCallback;
import com.lypaka.areamanager.API.RegionEvents.RegionEnterCallback;
import com.lypaka.areamanager.API.RegionEvents.RegionLeaveCallback;
import com.lypaka.areamanager.API.RegionEvents.RegionPermissionsCallback;
import com.lypaka.areamanager.Commands.AreaManagerCommand;
import com.lypaka.areamanager.Listeners.ServerLoadListener;
import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;
import com.lypaka.lypakautils.ConfigurationLoaders.ConfigUtils;
import com.lypaka.shadow.configurate.objectmapping.ObjectMappingException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AreaManager implements ModInitializer {

    public static final String MOD_ID = "areamanager";
    public static final String MOD_NAME = "AreaManager";
    public static final Logger logger = LogManager.getLogger(MOD_NAME);
    public static BasicConfigManager configManager;

    @Override
    public void onInitialize() {

        Path dir = ConfigUtils.checkDir(Paths.get("./config/areamanager"));
        String[] files = new String[]{"areamanager.conf"};
        configManager = new BasicConfigManager(files, dir, AreaManager.class, MOD_NAME, MOD_ID, logger);
        configManager.init();
        try {

            ConfigGetters.load();

        } catch (ObjectMappingException e) {

            throw new RuntimeException(e);

        }

        AreaManagerCommand.register();
        ServerLifecycleEvents.SERVER_STARTED.register(new ServerLoadListener());
        registerAreaManagerEvents();

    }

    private static void registerAreaManagerEvents() {

        AreaLeaveCallback.EVENT.register(((player, area, canLeave) -> canLeave));
        AreaPermissionsCallback.EVENT.register((player, area, permissions) -> true);

        AreaSwimCallback.TELEPORT_EVENT.register((player, area) -> {
            // Allow teleportation
            return true;
        });

        AreaSwimCallback.KILL_EVENT.register((player, area) -> {
            // Allow killing
            return true;
        });
        RegionEnterCallback.EVENT.register((player, region) -> true);
        RegionLeaveCallback.EVENT.register((player, region) -> true);
        RegionPermissionsCallback.EVENT.register((player, region, permissions) -> true);
        FinishedLoadingCallback.EVENT.register(() -> {
            System.out.println("[AreaManager] Finished loading.");
        });

    }

}
