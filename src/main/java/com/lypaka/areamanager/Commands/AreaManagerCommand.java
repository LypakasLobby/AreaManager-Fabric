package com.lypaka.areamanager.Commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.Arrays;
import java.util.List;

public class AreaManagerCommand {

    public static List<String> ALIASES = Arrays.asList("areamanager", "areas", "aman");

    public static void register() {

        CommandRegistrationCallback.EVENT.register((
                dispatcher,
                registryAccess,
                environment) -> {

            new WandCreateAreaCommand(dispatcher);
            new WandCreateRegionCommand(dispatcher);
            new CreateCommand(dispatcher);
            new ReloadCommand(dispatcher);
            new WandCommand(dispatcher);
            new WorldCommand(dispatcher);

        });

    }

}
