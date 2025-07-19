package com.lypaka.areamanager.Commands;

import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.PermissionHandler;
import com.lypaka.lypakautils.Handlers.WorldHandlers;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class WorldCommand {

    public WorldCommand (CommandDispatcher<ServerCommandSource> dispatcher) {

        for (String a : AreaManagerCommand.ALIASES) {

            dispatcher.register(
                    CommandManager.literal(a)
                            .then(
                                    CommandManager.literal("where")
                                            .executes(c -> {

                                                if (c.getSource().getEntity() instanceof ServerPlayerEntity) {

                                                    ServerPlayerEntity player = (ServerPlayerEntity) c.getSource().getEntity();
                                                    if (!PermissionHandler.hasPermission(player, "areamanager.command.admin")) {

                                                        player.sendMessage(FancyTextHandler.getFormattedText("&cYou don't have permission to use this command!"), false);
                                                        return 0;

                                                    }

                                                    String worldName = WorldHandlers.getWorldName(player);
                                                    player.sendMessage(FancyTextHandler.getFormattedText("&aWorld name: &e" + worldName), false);

                                                }

                                                return 1;

                                            })
                            )
            );

        }

    }

}
