package com.lypaka.areamanager.Regions;

import com.lypaka.areamanager.API.FinishedLoadingCallback;
import com.lypaka.areamanager.API.RegionEvents.RegionEnterCallback;
import com.lypaka.areamanager.API.RegionEvents.RegionLeaveCallback;
import com.lypaka.areamanager.API.RegionEvents.RegionPermissionsCallback;
import com.lypaka.areamanager.AreaManager;
import com.lypaka.areamanager.Areas.Area;
import com.lypaka.areamanager.Areas.AreaPermissions;
import com.lypaka.areamanager.ConfigGetters;
import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;
import com.lypaka.lypakautils.ConfigurationLoaders.ConfigUtils;
import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.lypakautils.Handlers.PermissionHandler;
import com.lypaka.lypakautils.Handlers.WorldHandlers;
import com.lypaka.shadow.configurate.objectmapping.ObjectMappingException;
import com.lypaka.shadow.google.common.reflect.TypeToken;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class RegionHandler {

    public static Map<String, Region> regionMap;
    public static Map<UUID, Region> playersLastKnownRegion = new HashMap<>();
    public static Map<Region, List<UUID>> playersInRegion = new HashMap<>();
    public static Map<Region, List<Area>> regionAreasMap = new HashMap<>();

    public static void teleportPlayerToRegionFailedToLeaveLocation (ServerPlayerEntity player, Region region) {

        RegionPermissions permissions = region.getPermissions();
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

    public static void teleportPlayerToRegionFailedToEnterLocation (ServerPlayerEntity player, Region region) {

        RegionPermissions permissions = region.getPermissions();
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

    public static boolean canPlayerEnterRegion (ServerPlayerEntity player, Region region) {

        RegionPermissions regionPermissions = region.getPermissions();
        boolean hasPermission = true;
        for (String p : regionPermissions.getEnterPermissions()) {

            if (!PermissionHandler.hasPermission(player, p)) {

                hasPermission = false;
                break;

            }

        }
        boolean enforcePermissions = RegionPermissionsCallback.EVENT.invoker().onPermissionCheck(player, region, regionPermissions);
        if (!enforcePermissions) hasPermission = true;
        boolean canEnter = RegionEnterCallback.EVENT.invoker().onRegionEnter(player, region);
        if (!canEnter) hasPermission = false;

        return hasPermission;

    }

    public static boolean canPlayerLeaveRegion (ServerPlayerEntity player, Region region) {

        boolean hasPermission = true;
        RegionPermissions permissions = region.getPermissions();
        for (String p : permissions.getLeavePermissions()) {

            if (!PermissionHandler.hasPermission(player, p)) {

                hasPermission = false;
                break;

            }

        }
        boolean enforcePermissions = RegionPermissionsCallback.EVENT.invoker().onPermissionCheck(player, region, permissions);
        if (!enforcePermissions) hasPermission = true;
        boolean canLeave = RegionLeaveCallback.EVENT.invoker().onRegionLeave(player, region);
        if (!canLeave) hasPermission = false;

        return hasPermission;

    }

    public static void removePlayerFromRegion (ServerPlayerEntity player, Region region) {

        playersInRegion.get(region).removeIf(e -> e.toString().equalsIgnoreCase(player.getUuid().toString()));

    }

    public static void addPlayerToRegion (ServerPlayerEntity player, Region region) {

        playersLastKnownRegion.put(player.getUuid(), region);
        List<UUID> uuids = new ArrayList<>();
        if (playersInRegion.containsKey(region)) uuids = playersInRegion.get(region);
        uuids.add(player.getUuid());
        playersInRegion.put(region, uuids);

    }

    public static void loadRegions() throws IOException, ObjectMappingException {

        regionMap = new HashMap<>();
        String[] regionFiles = new String[]{"regionSettings.conf"};
        String[] areaFiles = new String[]{"areaSettings.conf"};

        for (String regionName : ConfigGetters.regionNames) {

            AreaManager.logger.info("Loading region: " + regionName);
            Path dir = ConfigUtils.checkDir(Paths.get("./config/areamanager/regions/" + regionName));
            BasicConfigManager bcm = new BasicConfigManager(regionFiles, dir, AreaManager.class, AreaManager.MOD_NAME, AreaManager.MOD_ID, AreaManager.logger);
            bcm.init();

            String displayName = bcm.getConfigNode(0, "General-Settings", "Display-Name").getString();
            String maxX = bcm.getConfigNode(0, "General-Settings", "Location", "Max-X").getString();
            String maxY = bcm.getConfigNode(0, "General-Settings", "Location", "Max-Y").getString();
            String maxZ = bcm.getConfigNode(0, "General-Settings", "Location", "Max-Z").getString();
            String minX = bcm.getConfigNode(0, "General-Settings", "Location", "Min-X").getString();
            String minY = bcm.getConfigNode(0, "General-Settings", "Location", "Min-Y").getString();
            String minZ = bcm.getConfigNode(0, "General-Settings", "Location", "Min-Z").getString();
            String worldName = bcm.getConfigNode(0, "General-Settings", "Location", "World").getString();
            String enterPermissionMessage = bcm.getConfigNode(0, "General-Settings", "Permissions", "Enter", "Message").getString();
            List<String> enterPermissions = bcm.getConfigNode(0, "General-Settings", "Permissions", "Enter", "Permissions").getList(TypeToken.of(String.class));
            String enterTeleportLocation = bcm.getConfigNode(0, "General-Settings", "Permissions", "Enter", "Teleport").getString();
            String leavePermissionMessage = bcm.getConfigNode(0, "General-Settings", "Permissions", "Leave", "Message").getString();
            List<String> leavePermissions = bcm.getConfigNode(0, "General-Settings", "Permissions", "Leave", "Permissions").getList(TypeToken.of(String.class));
            String leaveTeleportLocation = bcm.getConfigNode(0, "General-Settings", "Permissions", "Leave", "Teleport").getString();
            RegionPermissions regionPermissions = new RegionPermissions(enterPermissionMessage, enterPermissions, enterTeleportLocation, leavePermissionMessage, leavePermissions, leaveTeleportLocation);
            List<String> areaNames = bcm.getConfigNode(0, "Locations").getList(TypeToken.of(String.class));
            List<Area> areas = new ArrayList<>();

            for (String area : areaNames) {

                AreaManager.logger.info("Loading area: " + area + " in region: " + regionName);
                Path areaDir = ConfigUtils.checkDir(Paths.get("./config/areamanager/regions/" + regionName + "/areas/" + area));
                BasicConfigManager areaBCM = new BasicConfigManager(areaFiles, areaDir, AreaManager.class, AreaManager.MOD_NAME, AreaManager.MOD_ID, AreaManager.logger);
                areaBCM.init();

                String areaDisplayName = areaBCM.getConfigNode(0, "Area-Display-Name").getString();
                int areaMaxX = areaBCM.getConfigNode(0, "Area-Location", "Max-X").getInt();
                int areaMaxY = areaBCM.getConfigNode(0, "Area-Location", "Max-Y").getInt();
                int areaMaxZ = areaBCM.getConfigNode(0, "Area-Location", "Max-Z").getInt();
                int areaMinX = areaBCM.getConfigNode(0, "Area-Location", "Min-X").getInt();
                int areaMinY = areaBCM.getConfigNode(0, "Area-Location", "Min-Y").getInt();
                int areaMinZ = areaBCM.getConfigNode(0, "Area-Location", "Min-Z").getInt();
                String areaWorldName = areaBCM.getConfigNode(0, "Area-Location", "World-Name").getString();
                //String enterMessage = areaBCM.getConfigNode(0, "Area-Messages", "Enter").getString();
                //String leaveMessage = areaBCM.getConfigNode(0, "Area-Messages", "Leave").getString();
                String enterTitle = "&eEntering: &a&l%plainName%";
                String enterSubtitle = "";
                String leaveTitle = "&eLeaving: &a&l%plainName%";
                String leaveSubtitle = "";
                if (areaBCM.getConfigNode(0, "Area-Messages", "Enter", "Title").isVirtual()) {

                    areaBCM.getConfigNode(0, "Area-Messages", "Enter").setValue(null);
                    areaBCM.getConfigNode(0, "Area-Messages", "Enter", "Title").setValue(enterTitle);
                    areaBCM.getConfigNode(0, "Area-Messages", "Enter", "Subtitle").setValue(enterSubtitle);
                    areaBCM.getConfigNode(0, "Area-Messages", "Leave").setValue(null);
                    areaBCM.getConfigNode(0, "Area-Messages", "Leave", "Title").setValue(leaveTitle);
                    areaBCM.getConfigNode(0, "Area-Messages", "Leave", "Subtitle").setValue(leaveSubtitle);
                    areaBCM.save();

                } else {

                    enterTitle = areaBCM.getConfigNode(0, "Area-Messages", "Enter", "Title").getString();
                    enterSubtitle = areaBCM.getConfigNode(0, "Area-Messages", "Enter", "Subtitle").getString();
                    leaveTitle = areaBCM.getConfigNode(0, "Area-Messages", "Leave", "Title").getString();
                    leaveSubtitle = areaBCM.getConfigNode(0, "Area-Messages", "Leave", "Subtitle").getString();

                }
                String plainName = areaBCM.getConfigNode(0, "Area-Plain-Name").getString();
                String areaEnterPermissionMessage = areaBCM.getConfigNode(0, "Permissions", "Enter", "Message").getString();
                List<String> areaEnterPermissions = areaBCM.getConfigNode(0, "Permissions", "Enter", "Permissions").getList(TypeToken.of(String.class));
                String areaEnterTeleportLocation = areaBCM.getConfigNode(0, "Permissions", "Enter", "Teleport").getString();
                String areaLeavePermissionMessage = areaBCM.getConfigNode(0, "Permissions", "Leave", "Message").getString();
                List<String> areaLeavePermissions = areaBCM.getConfigNode(0, "Permissions", "Leave", "Permissions").getList(TypeToken.of(String.class));
                String areaLeaveTeleportLocation = areaBCM.getConfigNode(0, "Permissions", "Leave", "Teleport").getString();

                AreaPermissions permissions = new AreaPermissions(areaEnterPermissionMessage, areaEnterPermissions, areaEnterTeleportLocation, areaLeavePermissionMessage, areaLeavePermissions, areaLeaveTeleportLocation);

                boolean killsForSwimming = areaBCM.getConfigNode(0, "Swim-Settings", "Kill-For-Swimming").getBoolean();
                boolean teleportsForSwimming = areaBCM.getConfigNode(0, "Swim-Settings", "Teleport-For-Swimming").getBoolean();
                int priority = areaBCM.getConfigNode(0, "Priority").getInt();
                int radius = areaBCM.getConfigNode(0, "Radius").getInt();
                int underground = areaBCM.getConfigNode(0, "Underground").getInt();

                Area a = new Area(area, areaDisplayName, areaMaxX, areaMaxY, areaMaxZ, areaMinX, areaMinY, areaMinZ, areaWorldName, enterTitle, enterSubtitle, leaveTitle, leaveSubtitle, plainName, killsForSwimming,
                        teleportsForSwimming, permissions, priority, radius, underground);

                areas.add(a);

            }

            Region region = new Region(regionName, displayName, maxX, maxY, maxZ, minX, minY, minZ, worldName, regionPermissions, areas, bcm);
            region.create();
            regionAreasMap.put(region, areas);
            regionMap.put(regionName, region);

        }

        FinishedLoadingCallback.EVENT.invoker().onFinishedLoading();

    }

    public static Region getFromName (String name) {

        Region r = null;
        for (Map.Entry<String, Region> e : regionMap.entrySet()) {

            if (e.getKey().equalsIgnoreCase(name)) {

                r = e.getValue();
                break;

            }

        }

        return r;

    }

    public static Region getRegionAtLocation (String world, int x, int y, int z) {

        Region r = null;
        for (Map.Entry<String, Region> regionMap : regionMap.entrySet()) {

            Region region = regionMap.getValue();
            String maxX = region.getMaxX();
            String maxY = region.getMaxY();
            String maxZ = region.getMaxZ();
            String minX = region.getMinX();
            String minY = region.getMinY();
            String minZ = region.getMinZ();
            String regionWorld = region.getWorldName();

            if (regionWorld.equalsIgnoreCase(world)) {

                if (minX.equals("*") && maxX.equals("*") || x >= Integer.parseInt(minX) && x <= Integer.parseInt(maxX)) {

                    if (minY.equals("*") && maxY.equals("*") || y >= Integer.parseInt(minY) && y <= Integer.parseInt(maxY)) {

                        if (minZ.equals("*") && maxZ.equals("*") || z >= Integer.parseInt(minZ) && z <= Integer.parseInt(maxZ)) {

                            r = region;
                            break;

                        }

                    }

                }

            }

        }

        return r;

    }

    public static Region getRegionAtPlayer (ServerPlayerEntity player) {

        Region r = null;
        int x = player.getBlockPos().getX();
        int y = player.getBlockPos().getY();
        int z = player.getBlockPos().getZ();
        String currentWorld = WorldHandlers.getWorldName(player);

        for (Map.Entry<String, Region> regionMap : regionMap.entrySet()) {

            Region region = regionMap.getValue();
            String maxX = region.getMaxX();
            String maxY = region.getMaxY();
            String maxZ = region.getMaxZ();
            String minX = region.getMinX();
            String minY = region.getMinY();
            String minZ = region.getMinZ();
            String regionWorld = region.getWorldName();

            if (regionWorld.equalsIgnoreCase(currentWorld)) {

                if (minX.equals("*") && maxX.equals("*") || x >= Integer.parseInt(minX) && x <= Integer.parseInt(maxX)) {

                    if (minY.equals("*") && maxY.equals("*") || y >= Integer.parseInt(minY) && y <= Integer.parseInt(maxY)) {

                        if (minZ.equals("*") && maxZ.equals("*") || z >= Integer.parseInt(minZ) && z <= Integer.parseInt(maxZ)) {

                            r = region;
                            break;

                        }

                    }

                }

            }

        }

        return r;

    }

}
