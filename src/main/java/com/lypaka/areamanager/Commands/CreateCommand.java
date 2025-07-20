package com.lypaka.areamanager.Commands;

import com.lypaka.areamanager.Areas.AreaHandler;
import com.lypaka.areamanager.Regions.Region;
import com.lypaka.areamanager.Regions.RegionHandler;
import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.PermissionHandler;
import com.lypaka.lypakautils.Handlers.WorldHandlers;
import com.lypaka.shadow.configurate.objectmapping.ObjectMappingException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class CreateCommand {

    // In case anyone was wondering...yes, I still hate Brigadier. And *then* and *then* and *then* and *then* and *then*...like a fucking 3rd grader telling my mom what happened at school today.
    public CreateCommand (CommandDispatcher<ServerCommandSource> dispatcher) {

        for (String a : AreaManagerCommand.ALIASES) {

            dispatcher.register(
                    CommandManager.literal(a)
                            .then(
                                    CommandManager.literal("create")
                                            .then(
                                                    CommandManager.literal("region")
                                                            .then(
                                                                    CommandManager.argument("name", StringArgumentType.word())
                                                                            .then(
                                                                                    CommandManager.argument("x1", StringArgumentType.word())
                                                                                            .then(
                                                                                                    CommandManager.argument("y1", StringArgumentType.word())
                                                                                                            .then(
                                                                                                                    CommandManager.argument("z1", StringArgumentType.word())
                                                                                                                            .then(
                                                                                                                                    CommandManager.argument("x2", StringArgumentType.word())
                                                                                                                                            .then(
                                                                                                                                                    CommandManager.argument("y2", StringArgumentType.word())
                                                                                                                                                            .then(
                                                                                                                                                                    CommandManager.argument("z2", StringArgumentType.word())
                                                                                                                                                                            .executes(c -> {

                                                                                                                                                                                if (c.getSource().getEntity() instanceof ServerPlayerEntity player) {

                                                                                                                                                                                    if (!PermissionHandler.hasPermission(player, "areamanager.command.admin")) {

                                                                                                                                                                                        player.sendMessage(FancyTextHandler.getFormattedText("&cYou don't have permission to use this command!"));
                                                                                                                                                                                        return 0;

                                                                                                                                                                                    }

                                                                                                                                                                                    String regionName = StringArgumentType.getString(c, "name");
                                                                                                                                                                                    if (!RegionHandler.regionMap.containsKey(regionName)) {

                                                                                                                                                                                        String worldName = WorldHandlers.getWorldName(player);
                                                                                                                                                                                        String x1 = StringArgumentType.getString(c, "x1");
                                                                                                                                                                                        String y1 = StringArgumentType.getString(c, "y1");
                                                                                                                                                                                        String z1 = StringArgumentType.getString(c, "z1");
                                                                                                                                                                                        String x2 = StringArgumentType.getString(c, "x2");
                                                                                                                                                                                        String y2 = StringArgumentType.getString(c, "y2");
                                                                                                                                                                                        String z2 = StringArgumentType.getString(c, "z2");

                                                                                                                                                                                        try {

                                                                                                                                                                                            RegionHandler.createNewRegion(regionName, worldName, x1, y1, z1, x2, y2, z2);
                                                                                                                                                                                            player.sendMessage(FancyTextHandler.getFormattedText("&aSuccessfully created Region: " + regionName));

                                                                                                                                                                                        } catch (

                                                                                                                                                                                                ObjectMappingException e) {

                                                                                                                                                                                            throw new RuntimeException(e);

                                                                                                                                                                                        }

                                                                                                                                                                                    } else {

                                                                                                                                                                                        player.sendMessage(FancyTextHandler.getFormattedText("&cRegion already exists!"));
                                                                                                                                                                                        return 0;

                                                                                                                                                                                    }

                                                                                                                                                                                }
                                                                                                                                                                                return 1;

                                                                                                                                                                            })

                                                                                                                                                            )

                                                                                                                                            )

                                                                                                                            )

                                                                                                            )

                                                                                            )

                                                                            )

                                                            )

                                            )
                                            .then(
                                                    CommandManager.literal("area")
                                                            .then(
                                                                    CommandManager.argument("region", StringArgumentType.word())
                                                                            .suggests(
                                                                                    (context, builder) -> CommandSource.suggestMatching(RegionHandler.regionMap.keySet(), builder)
                                                                            )
                                                                            .then(
                                                                                    CommandManager.argument("x1", IntegerArgumentType.integer())
                                                                                            .then(
                                                                                                    CommandManager.argument("y1", IntegerArgumentType.integer())
                                                                                                            .then(
                                                                                                                    CommandManager.argument("z1", IntegerArgumentType.integer())
                                                                                                                            .then(
                                                                                                                                    CommandManager.argument("x2", IntegerArgumentType.integer())
                                                                                                                                            .then(
                                                                                                                                                    CommandManager.argument("y2", IntegerArgumentType.integer())
                                                                                                                                                            .then(
                                                                                                                                                                    CommandManager.argument("z2", IntegerArgumentType.integer())
                                                                                                                                                                            .then(
                                                                                                                                                                                    CommandManager.argument("name", StringArgumentType.string())
                                                                                                                                                                                            .executes(c -> {

                                                                                                                                                                                                if (c.getSource().getEntity() instanceof ServerPlayerEntity player) {

                                                                                                                                                                                                    if (!PermissionHandler.hasPermission(player, "areamanager.command.admin")) {

                                                                                                                                                                                                        player.sendMessage(FancyTextHandler.getFormattedText("&cYou don't have permission to use this command!"));
                                                                                                                                                                                                        return 0;

                                                                                                                                                                                                    }

                                                                                                                                                                                                    String regionName = StringArgumentType.getString(c, "region");
                                                                                                                                                                                                    Region region = RegionHandler.getFromName(regionName);
                                                                                                                                                                                                    if (region != null) {

                                                                                                                                                                                                        String areaName = StringArgumentType.getString(c, "name");
                                                                                                                                                                                                        int x1 = IntegerArgumentType.getInteger(c, "x1");
                                                                                                                                                                                                        int y1 = IntegerArgumentType.getInteger(c, "y1");
                                                                                                                                                                                                        int z1 = IntegerArgumentType.getInteger(c, "z1");
                                                                                                                                                                                                        int x2 = IntegerArgumentType.getInteger(c, "x2");
                                                                                                                                                                                                        int y2 = IntegerArgumentType.getInteger(c, "y2");
                                                                                                                                                                                                        int z2 = IntegerArgumentType.getInteger(c, "z2");

                                                                                                                                                                                                        try {

                                                                                                                                                                                                            AreaHandler.createNewArea(player, areaName, region, x1, y1, z1, x2, y2, z2);

                                                                                                                                                                                                        } catch (

                                                                                                                                                                                                                ObjectMappingException e) {

                                                                                                                                                                                                            throw new RuntimeException(e);

                                                                                                                                                                                                        }

                                                                                                                                                                                                    }

                                                                                                                                                                                                }
                                                                                                                                                                                                return 1;

                                                                                                                                                                                            })
                                                                                                                                                                            )

                                                                                                                                                            )

                                                                                                                                            )

                                                                                                                            )

                                                                                                            )

                                                                                            )

                                                                            )

                                                            )

                                            )

                            )

            );

        }

    }

}
