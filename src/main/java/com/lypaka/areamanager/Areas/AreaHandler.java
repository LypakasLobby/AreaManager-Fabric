package com.lypaka.areamanager.Areas;

import com.lypaka.areamanager.API.AreaEvents.AreaEnterCallback;
import com.lypaka.areamanager.API.AreaEvents.AreaLeaveCallback;
import com.lypaka.areamanager.API.AreaEvents.AreaPermissionsCallback;
import com.lypaka.areamanager.API.AreaEvents.AreaSwimCallback;
import com.lypaka.areamanager.Regions.Region;
import com.lypaka.areamanager.Regions.RegionHandler;
import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.PermissionHandler;
import com.lypaka.lypakautils.Handlers.WorldHandlers;
import com.lypaka.lypakautils.PlayerLocationData.PlayerDataHandler;
import com.lypaka.lypakautils.PlayerLocationData.PlayerLocation;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class AreaHandler {

    public static Map<String, Map<Area, List<UUID>>> playersInArea = new HashMap<>();
    private static final Map<UUID, Integer> swimCounter = new HashMap<>();

    public static void runSwimCode (ServerPlayerEntity player, Area area) {

        if (player.isTouchingWater() || player.isSwimming() || isPlayerStandingAtBottomOfBodyOfWater(player)) {

            if (player.isCreative() || player.isSpectator()) return;

            if (player.getVehicle() == null) {

                if (area.killsForSwimming()) {

                    boolean allowKill = AreaSwimCallback.KILL_EVENT.invoker().onSwim(player, area);
                    if (allowKill) {

                        int counter = 0;
                        if (swimCounter.containsKey(player.getUuid())) {

                            counter = swimCounter.get(player.getUuid());

                        }
                        counter++;
                        if (counter >= 5) {

                            player.kill();
                            swimCounter.entrySet().removeIf(e -> e.getKey().toString().equalsIgnoreCase(player.getUuid().toString()));

                        } else {

                            swimCounter.put(player.getUuid(), counter);

                        }

                    }

                } else if (area.teleportsForSwimming()) {

                    boolean allowTeleport = AreaSwimCallback.TELEPORT_EVENT.invoker().onSwim(player, area);
                    if (allowTeleport) {

                        PlayerLocation playerLocation = PlayerDataHandler.playerLocationMap.get(player.getUuid());
                        int x = playerLocation.getLastLandLocation()[0];
                        int y = playerLocation.getLastLandLocation()[1];
                        int z = playerLocation.getLastLandLocation()[2];
                        player.setPosition(x, y, z);

                    }

                }

            } else {

                swimCounter.entrySet().removeIf(e -> e.getKey().toString().equalsIgnoreCase(player.getUuid().toString()));

            }

        } else {

            swimCounter.entrySet().removeIf(e -> e.getKey().toString().equalsIgnoreCase(player.getUuid().toString()));

        }

    }

    private static boolean isPlayerStandingAtBottomOfBodyOfWater (ServerPlayerEntity player) {

        BlockPos playerPos = player.getBlockPos();
        BlockState stateAtFeet = player.getWorld().getBlockState(playerPos);
        BlockState stateAboveFeet = player.getWorld().getBlockState(playerPos.up());
        boolean isInWater = stateAtFeet.getBlock() == Blocks.WATER;
        boolean isSubmerged = stateAboveFeet.getBlock() == Blocks.WATER;
        boolean isNotSwimming = !player.isSwimming() && !player.isSneaking() && player.isOnGround();
        boolean notFlying = !player.getAbilities().flying;

        return isInWater && isSubmerged && isNotSwimming && notFlying;

    }

    public static boolean canPlayerEnterArea (ServerPlayerEntity player, Area area) {

        AreaPermissions permissions = area.getPermissions();
        boolean hasAreaPermission = true;
        for (String p : permissions.getEnterPermissions()) {

            if (!PermissionHandler.hasPermission(player, p)) {

                hasAreaPermission = false;
                break;

            }

        }
        boolean hasPermission = AreaPermissionsCallback.EVENT.invoker().onPermissionCheck(player, area, permissions);
        if (hasPermission) hasAreaPermission = true;
        boolean canEnter = AreaEnterCallback.EVENT.invoker().onPlayerAreaEnter(player, area, hasAreaPermission);
        if (!canEnter) hasAreaPermission = false;
        return hasAreaPermission;

    }

    public static boolean canPlayerLeaveArea (ServerPlayerEntity player, Area area) {

        boolean hasPermission = true;
        AreaPermissions permissions = area.getPermissions();
        for (String p : permissions.getLeavePermissions()) {

            if (!PermissionHandler.hasPermission(player, p)) {

                hasPermission = false;
                break;

            }

        }
        boolean hasAreaPermission = AreaPermissionsCallback.EVENT.invoker().onPermissionCheck(player, area, permissions);
        if (hasAreaPermission) hasPermission = true;
        boolean canLeave = AreaLeaveCallback.EVENT.invoker().onPlayerAreaLeave(player, area, hasPermission);
        if (!canLeave) hasPermission = false;
        return hasPermission;

    }

    public static Area getAreaPlayerCantLeave (ServerPlayerEntity player) {

        Area area = null;
        boolean hasPermission = true;
        for (Area a : getAreasAtPlayer(player)) {

            AreaPermissions permissions = a.getPermissions();
            for (String p : permissions.getLeavePermissions()) {

                if (!PermissionHandler.hasPermission(player, p)) {

                    hasPermission = false;
                    break;

                }

            }
            boolean hasAreaPermission = AreaPermissionsCallback.EVENT.invoker().onPermissionCheck(player, area, permissions);
            if (hasAreaPermission) hasPermission = true;
            if (!hasPermission) {

                area = a;
                break;

            }

        }

        return area;

    }

    public static void teleportPlayerToAreaFailedToEnterLocation (ServerPlayerEntity player, Area area) {

        AreaPermissions permissions = area.getPermissions();
        if (!permissions.getEnterTeleportLocation().equalsIgnoreCase("x,y,z")) {

            int tpX = Integer.parseInt(permissions.getEnterTeleportLocation().split(",")[0]);
            int tpY = Integer.parseInt(permissions.getEnterTeleportLocation().split(",")[1]);
            int tpZ = Integer.parseInt(permissions.getEnterTeleportLocation().split(",")[2]);
            player.setPosition(tpX, tpY, tpZ);
            if (!permissions.getEnterMessage().equals("")) {

                player.sendMessage(FancyTextHandler.getFormattedText(permissions.getEnterMessage()), false);

            }

        }

    }

    public static void teleportPlayerToAreaFailedToLeaveLocation (ServerPlayerEntity player, Area area) {

        AreaPermissions permissions = area.getPermissions();
        if (!permissions.getLeaveTeleportLocation().equalsIgnoreCase("x,y,z")) {

            int tpX = Integer.parseInt(permissions.getLeaveTeleportLocation().split(",")[0]);
            int tpY = Integer.parseInt(permissions.getLeaveTeleportLocation().split(",")[1]);
            int tpZ = Integer.parseInt(permissions.getLeaveTeleportLocation().split(",")[2]);
            player.setPosition(tpX, tpY, tpZ);
            if (!permissions.getLeaveMessage().equals("")) {

                player.sendMessage(FancyTextHandler.getFormattedText(permissions.getLeaveMessage()), false);

            }

        }

    }

    public static void removePlayerFromArea (ServerPlayerEntity player, Area area) {

        Region region = RegionHandler.getRegionAtPlayer(player);
        if (region == null) {

            return;

        }
        if (area == null) {

            return;

        }
        playersInArea.get(region.getName()).get(area).removeIf(e -> {

            if (e.toString().equalsIgnoreCase(player.getUuid().toString())) {

                if (!area.getLeaveTitle().equalsIgnoreCase("")) {

                    player.networkHandler.sendPacket(new TitleS2CPacket(FancyTextHandler.getFormattedText(area.getLeaveTitle().replace("%plainName%", area.getPlainName()))));

                }
                if (!area.getLeaveSubtitle().equalsIgnoreCase("")) {

                    player.networkHandler.sendPacket(new SubtitleS2CPacket(FancyTextHandler.getFormattedText(area.getLeaveSubtitle().replace("%plainName%", area.getPlainName()))));

                }
                return true;

            }

            return false;

        });

    }

    public static void addPlayerToArea (ServerPlayerEntity player, Region region, Area area) {

        String regionName;
        if (region == null) {

            regionName = "None";

        } else {

            regionName = region.getName();

        }
        List<UUID> uuids = new ArrayList<>();
        Map<Area, List<UUID>> map = new HashMap<>();
        if (playersInArea.containsKey(regionName)) {

            map = playersInArea.get(regionName);
            if (map.containsKey(area)) uuids = map.get(area);

        }
        uuids.add(player.getUuid());
        map.put(area, uuids);
        playersInArea.put(regionName, map);

        if (!area.getEnterTitle().equalsIgnoreCase("")) {

            player.networkHandler.sendPacket(new TitleS2CPacket(FancyTextHandler.getFormattedText(area.getEnterTitle().replace("%plainName%", area.getPlainName()))));

        }
        if (!area.getEnterSubtitle().equalsIgnoreCase("")) {

            player.networkHandler.sendPacket(new SubtitleS2CPacket(FancyTextHandler.getFormattedText(area.getEnterSubtitle().replace("%plainName%", area.getPlainName()))));

        }

    }

    public static List<Area> getAreasAtPlayer (ServerPlayerEntity player) {

        return getFromLocation(player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ(), player.getWorld());

    }

    public static Area getFromName (String regionName, String areaName) {

        Area area = null;
        Region region = RegionHandler.regionMap.get(regionName);
        List<Area> areas = region.getAreas();
        for (Area a : areas) {

            if (a.getName().equalsIgnoreCase(areaName)) {

                area = a;
                break;

            }

        }

        return area;

    }

    public static List<Area> getSortedAreas (int x, int y, int z, World world) {

        List<Area> areas = getFromLocation(x, y, z, world);
        List<Area> sortedAreas = new ArrayList<>(areas.size());
        Map<Integer, Area> priorityMap = new HashMap<>();
        for (Area a : areas) {

            priorityMap.put(a.getPriority(), a);

        }
        List<Integer> priorities = new ArrayList<>(priorityMap.keySet());
        Collections.sort(priorities);
        for (int i = 0; i < priorities.size(); i++) {

            sortedAreas.add(i, priorityMap.get(priorities.get(i)));

        }

        return sortedAreas;

    }

    public static List<Area> getSortedAreas (ServerPlayerEntity player) {

        return getSortedAreas(player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ(), player.getWorld());

    }

    public static Area getHighestPriorityArea (int x, int y, int z, World world) {

        List<Area> areas = getFromLocation(x, y, z, world);
        Map<Integer, Area> priorityMap = new HashMap<>();
        for (Area a : areas) {

            priorityMap.put(a.getPriority(), a);

        }
        List<Integer> priorities = new ArrayList<>(priorityMap.keySet());
        Collections.sort(priorities);
        return priorityMap.get(priorities.get(0));

    }

    public static Area getHighestPriorityArea (ServerPlayerEntity player) {

        return getHighestPriorityArea(player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ(), player.getWorld());

    }

    public static Area getLowestPriorityArea (int x, int y, int z, World world) {

        try {

            List<Area> areas = getFromLocation(x, y, z, world);
            Map<Integer, Area> priorityMap = new HashMap<>();
            for (Area a : areas) {

                priorityMap.put(a.getPriority(), a);

            }
            List<Integer> priorities = new ArrayList<>(priorityMap.keySet());
            Collections.sort(priorities);
            return priorityMap.get(priorities.get(priorities.size() - 1));

        } catch (IndexOutOfBoundsException er) {

            return null;

        }

    }

    public static Area getLowestPriorityArea (ServerPlayerEntity player) {

        return getLowestPriorityArea(player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ(), player.getWorld());

    }

    public static List<Area> getFromLocation (int x, int y, int z, World world) {

        List<Area> areas = new ArrayList<>();
        String currentWorld = WorldHandlers.getWorldName(world);
        for (Map.Entry<String, Region> regionMap : RegionHandler.regionMap.entrySet()) {

            Region r = regionMap.getValue();
            String maxX = r.getMaxX();
            String maxY = r.getMaxY();
            String maxZ = r.getMaxZ();
            String minX = r.getMinX();
            String minY = r.getMinY();
            String minZ = r.getMinZ();
            String worldName = r.getWorldName();
            if (worldName.equalsIgnoreCase(currentWorld)) {

                if (minX.equals("*") && maxX.equals("*") || x >= Integer.parseInt(minX) && x <= Integer.parseInt(maxX)) {

                    if (minY.equals("*") && maxY.equals("*") || y >= Integer.parseInt(minY) && y <= Integer.parseInt(maxY)) {

                        if (minZ.equals("*") && maxZ.equals("*") || z >= Integer.parseInt(minZ) && z <= Integer.parseInt(maxZ)) {

                            List<Area> regionAreas = r.getAreas();
                            for (Area a : regionAreas) {

                                int areaMaxX = a.getMaxX();
                                int areaMaxY = a.getMaxY();
                                int areaMaxZ = a.getMaxZ();
                                int areaMinX = a.getMinX();
                                int areaMinY = a.getMinY();
                                int areaMinZ = a.getMinZ();
                                String areaWorldName = a.getWorldName();

                                if (currentWorld.equalsIgnoreCase(areaWorldName)) {

                                    if (x >= areaMinX && x <= areaMaxX) {

                                        if (y >= areaMinY && y <= areaMaxY) {

                                            if (z >= areaMinZ && z <= areaMaxZ) {

                                                areas.add(a);

                                            }

                                        }

                                    }

                                }

                            }

                            break;

                        }

                    }

                }

            }

        }

        return areas;

    }

    public static boolean areaHasPlayer (Region region, Area area, UUID uuid) {

        boolean has = false;
        if (playersInArea.containsKey(region.getName())) {

            Map<Area, List<UUID>> players = playersInArea.get(region.getName());
            if (players.containsKey(area)) {

                List<UUID> uuids = players.get(area);
                has = uuids.contains(uuid);

            }

        }

        return has;

    }

    public static boolean areaHasPlayer (Region region, Area area, ServerPlayerEntity player) {

        return areaHasPlayer(region, area, player.getUuid());

    }

}
