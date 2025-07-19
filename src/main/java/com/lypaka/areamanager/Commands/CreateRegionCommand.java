package com.lypaka.areamanager.Commands;

import com.lypaka.areamanager.Wand.WandHandler;
import com.lypaka.areamanager.Wand.WandPOS;
import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.PermissionHandler;
import com.lypaka.shadow.configurate.objectmapping.ObjectMappingException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class CreateRegionCommand {

    public CreateRegionCommand(CommandDispatcher<ServerCommandSource> dispatcher) {

        for (String a : AreaManagerCommand.ALIASES) {

            dispatcher.register(
                    CommandManager.literal(a)
                            .then(
                                    CommandManager.literal("createregion")
                                            .then(
                                                    CommandManager.argument("name", StringArgumentType.word())
                                                            .executes(c -> {

                                                                if (c.getSource().getEntity() instanceof ServerPlayerEntity) {

                                                                    ServerPlayerEntity player = (ServerPlayerEntity) c.getSource().getEntity();
                                                                    if (!PermissionHandler.hasPermission(player, "areamanager.command.admin")) {

                                                                        player.sendMessage(FancyTextHandler.getFormattedText("&cYou don't have permission to use this command!"), false);
                                                                        return 0;

                                                                    }
                                                                    if (WandHandler.wandMap.containsKey(player.getUuid())) {

                                                                        List<WandPOS> posList = WandHandler.wandMap.get(player.getUuid());
                                                                        if (posList.size() > 1) {

                                                                            String name = StringArgumentType.getString(c, "name");
                                                                            try {

                                                                                WandHandler.createRegion(player, name);

                                                                            } catch (ObjectMappingException e) {

                                                                                throw new RuntimeException(e);

                                                                            }

                                                                        } else {

                                                                            player.sendMessage(FancyTextHandler.getFormattedText("&cNot enough positions set!"), false);

                                                                        }

                                                                    } else {

                                                                        player.sendMessage(FancyTextHandler.getFormattedText("&cNo positions set! Use &e\"/areas wand\" to set some!"), false);

                                                                    }

                                                                }

                                                                return 0;

                                                            })
                                            )
                            )
            );

        }

    }

}
