package com.lypaka.areamanager.Wand;

import com.lypaka.areamanager.AreaManager;
import com.lypaka.areamanager.Areas.Area;
import com.lypaka.areamanager.Areas.AreaPermissions;
import com.lypaka.areamanager.ConfigGetters;
import com.lypaka.areamanager.Regions.Region;
import com.lypaka.areamanager.Regions.RegionHandler;
import com.lypaka.areamanager.Regions.RegionPermissions;
import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;
import com.lypaka.lypakautils.ConfigurationLoaders.ConfigUtils;
import com.lypaka.lypakautils.Handlers.FancyTextHandler;
import com.lypaka.shadow.configurate.objectmapping.ObjectMappingException;
import com.lypaka.shadow.google.common.reflect.TypeToken;
import net.minecraft.server.network.ServerPlayerEntity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class WandHandler {

    public static Map<UUID, List<WandPOS>> wandMap = new HashMap<>();

    public static void addPosition (ServerPlayerEntity player, int x, int y, int z, String world, int position) {

        List<WandPOS> list = new ArrayList<>(2);
        if (wandMap.containsKey(player.getUuid())) {

            list = wandMap.get(player.getUuid());

        }
        WandPOS pos = new WandPOS(world, x, y, z);
        if (position == 0) list.add(0, pos);
        if (position == 1) list.add(1, pos);

        wandMap.put(player.getUuid(), list);
        String posNum = "POS 1";
        if (position == 1) posNum = "POS 2";
        player.sendMessage(FancyTextHandler.getFormattedText("&e" + posNum + " set at: " + x + ", " + y + ", " + z), false);
        if (list.size() == 2) {

            player.sendMessage(FancyTextHandler.getFormattedText("&aBoth positions set! Use &e\"/areas createarea <region> <area>\" &ato create a new Area!"), false);
            player.sendMessage(FancyTextHandler.getFormattedText("&aOr use &e\"/areas createregion <name>\" &ato create a new Region!"), false);

        }

    }

    public static void clearPositions (ServerPlayerEntity player) {

        wandMap.entrySet().removeIf(e -> e.getKey().toString().equalsIgnoreCase(player.getUuid().toString()));
        player.sendMessage(FancyTextHandler.getFormattedText("&ePositions cleared."), false);

    }

    public static void createArea (ServerPlayerEntity player, String area, String regionName) throws ObjectMappingException {

        List<WandPOS> posList = wandMap.get(player.getUuid());
        WandPOS pos1 = posList.get(0);
        WandPOS pos2 = posList.get(1);

        if (pos1.getWorld().equalsIgnoreCase(pos2.getWorld())) {

            if (RegionHandler.regionMap.containsKey(regionName)) {

                String[] areaFiles = new String[]{"areaSettings.conf"};
                Region region = RegionHandler.regionMap.get(regionName);
                List<String> areaNames = new ArrayList<>(region.getConfigManager().getConfigNode(0, "Locations").getList(TypeToken.of(String.class)));
                areaNames.add(area);
                region.getConfigManager().getConfigNode(0, "Locations").setValue(areaNames);
                region.getConfigManager().save();
                List<Area> areas = region.getAreas();
                int maxX = Math.max(pos1.getX(), pos2.getX());
                int maxY = Math.max(pos1.getY(), pos2.getY());
                int maxZ = Math.max(pos1.getZ(), pos2.getZ());
                int minX = Math.min(pos1.getX(), pos2.getX());
                int minY = Math.min(pos1.getY(), pos2.getY());
                int minZ = Math.min(pos1.getZ(), pos2.getZ());
                String world = pos1.getWorld();

                AreaManager.logger.info("Loading area: " + area + " in region: " + regionName);
                Path areaDir = ConfigUtils.checkDir(Paths.get("./config/areamanager/regions/" + regionName + "/areas/" + area));
                BasicConfigManager areaBCM = new BasicConfigManager(areaFiles, areaDir, AreaManager.class, AreaManager.MOD_NAME, AreaManager.MOD_ID, AreaManager.logger);
                areaBCM.init();

                String areaDisplayName = areaBCM.getConfigNode(0, "Area-Display-Name").getString();
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

                areaBCM.getConfigNode(0, "Area-Location", "Max-X").setValue(maxX);
                areaBCM.getConfigNode(0, "Area-Location", "Max-Y").setValue(maxY);
                areaBCM.getConfigNode(0, "Area-Location", "Max-Z").setValue(maxZ);
                areaBCM.getConfigNode(0, "Area-Location", "Min-X").setValue(minX);
                areaBCM.getConfigNode(0, "Area-Location", "Min-Y").setValue(minY);
                areaBCM.getConfigNode(0, "Area-Location", "Min-Z").setValue(minZ);
                areaBCM.getConfigNode(0, "Area-Location", "World-Name").setValue(world);
                areaBCM.save();

                Area a =new Area(area, areaDisplayName, maxX, maxY, maxZ, minX, minY, minZ, world, enterTitle, enterSubtitle, leaveTitle, leaveSubtitle, plainName, killsForSwimming,
                        teleportsForSwimming, permissions, priority, radius, underground);

                areas.add(a);

                player.sendMessage(FancyTextHandler.getFormattedText("&aSuccessfully created Area: " + area + " in Region: " + regionName + "!"), false);

            } else {

                player.sendMessage(FancyTextHandler.getFormattedText("Region " + regionName + " does not exist! Can't add Area to nonexistent Region!"), false);

            }

        } else {

            player.sendMessage(FancyTextHandler.getFormattedText("&cRegions and areas both need to be in the same world!"), false);

        }

        wandMap.remove(player.getUuid());

    }

    public static void createRegion (ServerPlayerEntity player, String name) throws ObjectMappingException {

        List<WandPOS> posList = wandMap.get(player.getUuid());
        WandPOS pos1 = posList.get(0);
        WandPOS pos2 = posList.get(1);

        if (pos1.getWorld().equalsIgnoreCase(pos2.getWorld())) {

            String maxX = String.valueOf(Math.max(pos1.getX(), pos2.getX()));
            String maxY = String.valueOf(Math.max(pos1.getY(), pos2.getY()));
            String maxZ = String.valueOf(Math.max(pos1.getZ(), pos2.getZ()));
            String minX = String.valueOf(Math.min(pos1.getX(), pos2.getX()));
            String minY = String.valueOf(Math.min(pos1.getY(), pos2.getY()));
            String minZ = String.valueOf(Math.min(pos1.getZ(), pos2.getZ()));
            String world = pos1.getWorld();

            String[] regionFiles = new String[]{"regionSettings.conf"};
            String[] areaFiles = new String[]{"areaSettings.conf"};
            ConfigGetters.regionNames.add(name);
            AreaManager.configManager.getConfigNode(0, "Regions").setValue(ConfigGetters.regionNames);
            AreaManager.configManager.save();

            AreaManager.logger.info("Loading region: " + name);
            Path dir = ConfigUtils.checkDir(Paths.get("./config/areamanager/regions/" + name));
            BasicConfigManager bcm = new BasicConfigManager(regionFiles, dir, AreaManager.class, AreaManager.MOD_NAME, AreaManager.MOD_ID, AreaManager.logger);
            bcm.init();

            String displayName = bcm.getConfigNode(0, "General-Settings", "Display-Name").getString();
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

                AreaManager.logger.info("Loading area: " + area + " in region: " + name);
                Path areaDir = ConfigUtils.checkDir(Paths.get("./config/areamanager/regions/" + name + "/areas/" + area));
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

            Region region = new Region(name, displayName, maxX, maxY, maxZ, minX, minY, minZ, world, regionPermissions, areas, bcm);
            region.create();
            RegionHandler.regionAreasMap.put(region, areas);
            RegionHandler.regionMap.put(name, region);
            bcm.getConfigNode(0, "General-Settings", "Location", "Max-X").setValue(maxX);
            bcm.getConfigNode(0, "General-Settings", "Location", "Max-Y").setValue(maxY);
            bcm.getConfigNode(0, "General-Settings", "Location", "Max-Z").setValue(maxZ);
            bcm.getConfigNode(0, "General-Settings", "Location", "Min-X").setValue(minX);
            bcm.getConfigNode(0, "General-Settings", "Location", "Min-Y").setValue(minY);
            bcm.getConfigNode(0, "General-Settings", "Location", "Min-Z").setValue(minZ);
            bcm.getConfigNode(0, "General-Settings", "Location", "World").setValue(world);
            bcm.save();

            player.sendMessage(FancyTextHandler.getFormattedText("&aSuccessfully created Region: " + name + "!"), false);

        } else {

            player.sendMessage(FancyTextHandler.getFormattedText("&cRegions and areas both need to be in the same world!"), false);

        }

        wandMap.remove(player.getUuid());

    }

}
