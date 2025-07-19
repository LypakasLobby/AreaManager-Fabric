package com.lypaka.areamanager.Listeners;

import com.lypaka.areamanager.Areas.Area;
import com.lypaka.areamanager.Areas.AreaHandler;
import com.lypaka.areamanager.Regions.Region;
import com.lypaka.areamanager.Regions.RegionHandler;
import com.lypaka.lypakautils.API.PlayerWaterMovementCallback;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Used for the swim stuff, for teleportation/killing in areas and stuff
 */
public class SwimListener implements PlayerWaterMovementCallback {

    @Override
    public void onPlayerMove (ServerPlayerEntity player, int steps) {

        Region playerRegion = RegionHandler.getRegionAtPlayer(player);

        // Handles what I'm calling either me being dumb or a special-case first-time running code after start up issue of RegionHandler map being empty
        if (RegionHandler.playersInRegion.isEmpty()) {

            if (playerRegion == null) return; // this check prevents a crash if the player is moving in a "no-man's land" area
            if (RegionHandler.canPlayerEnterRegion(player, playerRegion)) {

                RegionHandler.addPlayerToRegion(player, playerRegion);
                List<Area> playersCurrentAreas = AreaHandler.getSortedAreas(player);
                Area areaToAddTo = null;
                for (Area a : playersCurrentAreas) {

                    if (AreaHandler.canPlayerEnterArea(player, a)) {

                        areaToAddTo = a;
                        break;

                    }

                }
                if (areaToAddTo != null) {

                    AreaHandler.addPlayerToArea(player, playerRegion, areaToAddTo);

                } else {

                    Area lowestArea = AreaHandler.getLowestPriorityArea(player);
                    if (lowestArea != null) { // this check prevents a crash in the event player moves into a null area

                        AreaHandler.teleportPlayerToAreaFailedToEnterLocation(player, lowestArea);

                    }

                }

            } else {

                RegionHandler.teleportPlayerToRegionFailedToEnterLocation(player, playerRegion);

            }

        } else {

            Region lastKnownRegion = RegionHandler.playersLastKnownRegion.get(player.getUuid());
            String lastRegionName = "None";
            String currentRegionName = "None";
            if (lastKnownRegion != null) lastRegionName = lastKnownRegion.getName();
            if (playerRegion != null) currentRegionName = playerRegion.getName();
            if (lastKnownRegion != null) {

                if (!lastRegionName.equalsIgnoreCase(currentRegionName)) {

                    if (RegionHandler.canPlayerLeaveRegion(player, lastKnownRegion)) {

                        if (AreaHandler.playersInArea.containsKey(lastKnownRegion.getName())) {

                            List<Area> playerAreas = new ArrayList<>();
                            Map<Area, List<UUID>> map = AreaHandler.playersInArea.get(lastKnownRegion.getName());
                            for (Map.Entry<Area, List<UUID>> entry : map.entrySet()) {

                                if (entry.getValue().contains(player.getUuid())) playerAreas.add(entry.getKey());

                            }
                            Area areaCantLeave = null;
                            for (Area a : playerAreas) {

                                if (!AreaHandler.canPlayerLeaveArea(player, a)) {

                                    areaCantLeave = a;
                                    break;

                                }

                            }

                            if (areaCantLeave == null) { // no Area is preventing the player from leaving the region, so let them try to leave

                                if (playerRegion != null) { // prevents trying to run permission checks on a potentially null region (no-man's land)

                                    if (RegionHandler.canPlayerEnterRegion(player, playerRegion)) { // check if player can enter region trying to enter

                                        List<Area> currentAreas = AreaHandler.getAreasAtPlayer(player);
                                        Area areaPreventedFromEntering = null;
                                        for (Area a : currentAreas) {

                                            if (!AreaHandler.canPlayerEnterArea(player, a)) {

                                                areaPreventedFromEntering = a;
                                                break;

                                            }

                                        }
                                        if (areaPreventedFromEntering == null) { // the area(s) at the player's current location in the new region are not preventing entry

                                            RegionHandler.removePlayerFromRegion(player, lastKnownRegion);
                                            for (Area a : playerAreas) {

                                                AreaHandler.removePlayerFromArea(player, a);

                                            }
                                            RegionHandler.addPlayerToRegion(player, playerRegion);
                                            Area lowestPriorityArea = AreaHandler.getLowestPriorityArea(player);
                                            if (lowestPriorityArea != null) { // this check prevents a crash in the event player moves into a null area

                                                AreaHandler.addPlayerToArea(player, playerRegion, lowestPriorityArea);

                                            }

                                        } else { // player can enter region, but not the area in this region, teleport to region failed to enter location (block glitching)

                                            RegionHandler.teleportPlayerToRegionFailedToEnterLocation(player, playerRegion);

                                        }

                                    } else { // player cannot enter region

                                        RegionHandler.teleportPlayerToRegionFailedToEnterLocation(player, playerRegion);

                                    }

                                } else { // player is leaving a region but not entering a new one (entering no-man's land)

                                    RegionHandler.removePlayerFromRegion(player, lastKnownRegion);
                                    for (Area a : playerAreas) {

                                        AreaHandler.removePlayerFromArea(player, a);

                                    }
                                    RegionHandler.addPlayerToRegion(player, null); // setting null here is safe due to lines 69-72

                                }

                            } else { // there's an area preventing the player from leaving, teleport to that area's failed to leave location

                                AreaHandler.teleportPlayerToAreaFailedToLeaveLocation(player, areaCantLeave);

                            }

                        }

                    } else { // player cannot leave region due to permission checks, teleport to failed to leave region location

                        RegionHandler.teleportPlayerToRegionFailedToLeaveLocation(player, lastKnownRegion);

                    }

                } else { // player is moving from one area to another while maintaining the same region, check areas

                    List<Area> playerAreas = new ArrayList<>();
                    List<Area> sortedAreas = AreaHandler.getSortedAreas(player);
                    try {

                        if (AreaHandler.playersInArea.containsKey(lastKnownRegion.getName())) {

                            Map<Area, List<UUID>> map = AreaHandler.playersInArea.get(lastKnownRegion.getName());
                            for (Map.Entry<Area, List<UUID>> entry : map.entrySet()) {

                                if (entry.getValue().contains(player.getUuid())) playerAreas.add(entry.getKey());

                            }
                            for (Area a : sortedAreas) {

                                for (Area area : playerAreas) {

                                    if (a.getName().equalsIgnoreCase(area.getName())) { // player is moving in the same area, not changing areas

                                        AreaHandler.runSwimCode(player, area); // checks for no swimming stuff
                                        return; // if we don't return here the code runs the "enter" code for the same area over and over again

                                    }

                                }

                            }
                            Area areaPreventedFromLeaving = null;
                            for (Area a : playerAreas) {

                                if (!AreaHandler.canPlayerLeaveArea(player, a)) {

                                    areaPreventedFromLeaving = a;
                                    break;

                                }

                            }
                            if (areaPreventedFromLeaving == null) { // no areas are preventing the player from leaving

                                Area areaPreventedFromEntering = null;
                                for (Area a : sortedAreas) {

                                    if (!AreaHandler.canPlayerEnterArea(player, a)) {

                                        areaPreventedFromEntering = a;
                                        break;

                                    }

                                }
                                if (areaPreventedFromEntering == null) { // no areas are preventing the player from entering, move them

                                    for (Area a : playerAreas) {

                                        AreaHandler.removePlayerFromArea(player, a);

                                    }
                                    for (Area a : sortedAreas) {

                                        AreaHandler.addPlayerToArea(player, playerRegion, a);

                                    }

                                } else { // an area is preventing the player from entering, teleport to that area's failed to enter location

                                    AreaHandler.teleportPlayerToAreaFailedToEnterLocation(player, areaPreventedFromEntering);

                                }

                            } else { // an area is preventing the player from leaving, teleport to that area's failed to leave location

                                AreaHandler.teleportPlayerToAreaFailedToLeaveLocation(player, areaPreventedFromLeaving);

                            }

                        } else {

                            if (sortedAreas.size() > 0) {

                                for (Area a : sortedAreas) {

                                    AreaHandler.addPlayerToArea(player, playerRegion, a);

                                }

                            }

                        }

                    } catch (NullPointerException er) {

                        // do nothing, player is moving in an undefined area "no-man's land" inside a region

                    }

                }

            } else { // player is moving from no-man's land into a region

                if (playerRegion != null) { // player is attempting to enter a region from no-man's land

                    if (RegionHandler.canPlayerEnterRegion(player, playerRegion)) { // check if player can enter region trying to enter

                        List<Area> currentAreas = AreaHandler.getAreasAtPlayer(player);
                        Area areaPreventedFromEntering = null;
                        for (Area a : currentAreas) {

                            if (!AreaHandler.canPlayerEnterArea(player, a)) {

                                areaPreventedFromEntering = a;
                                break;

                            }

                        }
                        if (areaPreventedFromEntering == null) { // the area(s) at the player's current location in the new region are not preventing entry

                            RegionHandler.addPlayerToRegion(player, playerRegion);
                            Area lowestPriorityArea = AreaHandler.getLowestPriorityArea(player);
                            if (lowestPriorityArea != null) { // this check prevents a crash in the event player moves into a null area

                                AreaHandler.addPlayerToArea(player, playerRegion, lowestPriorityArea);

                            }

                        } else { // player can enter region, but not the area in this region, teleport to region failed to enter location (block glitching)

                            RegionHandler.teleportPlayerToRegionFailedToEnterLocation(player, playerRegion);

                        }

                    } else { // player cannot leave region due to permission checks, teleport to world spawn (due to not having a teleport location in no-man's land)

                        int x = player.getServerWorld().getSpawnPos().getX();
                        int y = player.getServerWorld().getSpawnPos().getY();
                        int z = player.getServerWorld().getSpawnPos().getZ();
                        player.setPosition(x, y, z);

                    }

                } else { // player is moving around in no-man's land, do nothing

                    return;

                }

            }

        }

    }

}
