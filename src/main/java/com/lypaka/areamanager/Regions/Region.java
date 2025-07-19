package com.lypaka.areamanager.Regions;

import com.lypaka.areamanager.Areas.Area;
import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;

import java.util.List;

public class Region {

    private final String name;
    private final String displayName;
    private final String maxX;
    private final String maxY;
    private final String maxZ;
    private final String minX;
    private final String minY;
    private final String minZ;
    private final String worldName;
    private final RegionPermissions permissions;
    private final List<Area> areas;
    private final BasicConfigManager bcm;

    public Region (String name, String displayName, String maxX, String maxY, String maxZ, String minX, String minY, String minZ, String worldName, RegionPermissions permissions, List<Area> areas, BasicConfigManager bcm) {

        this.name = name;
        this.displayName = displayName;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.worldName = worldName;
        this.permissions = permissions;
        this.areas = areas;
        this.bcm = bcm;

    }

    public void create() {

        RegionHandler.regionMap.put(this.name, this);

    }

    public String getName() {

        return this.name;

    }

    public String getDisplayName() {

        return this.displayName;

    }

    public String getMaxX() {

        return this.maxX;

    }

    public String getMaxY() {

        return this.maxY;

    }

    public String getMaxZ() {

        return this.maxZ;

    }

    public String getMinX() {

        return this.minX;

    }

    public String getMinY() {

        return this.minY;

    }

    public String getMinZ() {

        return this.minZ;

    }

    public String getWorldName() {

        return this.worldName;

    }

    public RegionPermissions getPermissions() {

        return this.permissions;

    }

    public List<Area> getAreas() {

        return this.areas;

    }

    public BasicConfigManager getConfigManager() {

        return this.bcm;

    }

}
