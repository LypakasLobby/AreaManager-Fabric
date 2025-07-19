package com.lypaka.areamanager.Commands;

import com.lypaka.areamanager.AreaManager;
import com.lypaka.areamanager.ConfigGetters;
import com.lypaka.areamanager.Regions.RegionHandler;
import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.PermissionHandler;
import com.lypaka.shadow.configurate.objectmapping.ObjectMappingException;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.IOException;

public class ReloadCommand {

    public ReloadCommand (CommandDispatcher<ServerCommandSource> dispatcher) {

        for (String a : AreaManagerCommand.ALIASES) {

            dispatcher.register(
                    CommandManager.literal(a)
                            .then(
                                    CommandManager.literal("reload")
                                            .executes(c -> {

                                                if (c.getSource().getEntity() instanceof ServerPlayerEntity) {

                                                    ServerPlayerEntity player = (ServerPlayerEntity) c.getSource().getEntity();
                                                    if (!PermissionHandler.hasPermission(player, "areamanager.command.admin")) {

                                                        player.sendMessage(FancyTextHandler.getFormattedText("&cYou don't have permission to use this command!"), false);
                                                        return 0;

                                                    }

                                                }

                                                try {

                                                    AreaManager.configManager.load();
                                                    ConfigGetters.load();
                                                    RegionHandler.loadRegions();
                                                    c.getSource().sendMessage(FancyTextHandler.getFormattedText("&aSuccessfully reloaded AreaManager!"));

                                                } catch (ObjectMappingException | IOException e) {

                                                    e.printStackTrace();

                                                }

                                                return 1;

                                            })
                            )
            );

        }

    }

}
