package com.lypaka.areamanager.Commands;

import com.lypaka.areamanager.ConfigGetters;
import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.PermissionHandler;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class WandCommand {

    public WandCommand (CommandDispatcher<ServerCommandSource> dispatcher) {

        for (String a : AreaManagerCommand.ALIASES) {

            dispatcher.register(
                    CommandManager.literal(a)
                            .then(
                                    CommandManager.literal("wand")
                                            .executes(c -> {

                                                if (c.getSource().getEntity() instanceof ServerPlayerEntity) {

                                                    ServerPlayerEntity player = (ServerPlayerEntity) c.getSource().getEntity();
                                                    if (!PermissionHandler.hasPermission(player, "areamanager.command.admin")) {

                                                        player.sendMessage(FancyTextHandler.getFormattedText("&cYou don't have permission to use this command!"), false);
                                                        return 0;

                                                    }

                                                    player.giveItemStack(ConfigGetters.getWand());
                                                    player.sendMessage(FancyTextHandler.getFormattedText("&eLeft click to set POS 1"), false);
                                                    player.sendMessage(FancyTextHandler.getFormattedText("&eRight click to set POS 2"), false);
                                                    player.sendMessage(FancyTextHandler.getFormattedText("&eShift left click to clear"), false);

                                                }

                                                return 1;

                                            })
                            )
            );

        }

    }

}
