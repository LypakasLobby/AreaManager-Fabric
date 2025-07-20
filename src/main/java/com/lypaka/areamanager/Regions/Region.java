package com.lypaka.areamanager.Regions;

import com.lypaka.areamanager.Areas.Area;
import com.lypaka.lypakautils.ConfigurationLoaders.BasicConfigManager;

import java.util.List;

public class Region {

    private final String name;
    private final String displayName;
    private final String x1;
    private final String y1;
    private final String z1;
    private final String x2;
    private final String y2;
    private final String z2;
    private final String worldName;
    private final RegionPermissions permissions;
    private final List<Area> areas;
    private final BasicConfigManager bcm;

    public Region (String name, String displayName, String x1, String y1, String z1, String x2, String y2, String z2, String worldName, RegionPermissions permissions, List<Area> areas, BasicConfigManager bcm) {

        this.name = name;
        this.displayName = displayName;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
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

        String returnValue = this.x1;
        try {

            int tryX1 = Integer.parseInt(this.x1);
            int tryX2 = Integer.parseInt(this.x2);
            returnValue = String.valueOf(Math.max(tryX1, tryX2));

        } catch (Exception e) {

            // do nothing, will fail if using *

        }
        return returnValue;

    }

    public String getMaxY() {

        String returnValue = this.y1;
        try {

            int tryY1 = Integer.parseInt(this.y1);
            int tryY2 = Integer.parseInt(this.y2);
            returnValue = String.valueOf(Math.max(tryY1, tryY2));

        } catch (Exception e) {

            // do nothing, will fail if using *

        }
        return returnValue;

    }

    public String getMaxZ() {

        String returnValue = this.z1;
        try {

            int tryZ1 = Integer.parseInt(this.z1);
            int tryZ2 = Integer.parseInt(this.z2);
            returnValue = String.valueOf(Math.max(tryZ1, tryZ2));

        } catch (Exception e) {

            // do nothing, will fail if using *

        }
        return returnValue;

    }

    public String getMinX() {

        String returnValue = this.x1;
        try {

            int tryX1 = Integer.parseInt(this.x1);
            int tryX2 = Integer.parseInt(this.x2);
            returnValue = String.valueOf(Math.min(tryX1, tryX2));

        } catch (Exception e) {

            // do nothing, will fail if using *

        }
        return returnValue;

    }

    public String getMinY() {

        String returnValue = this.y1;
        try {

            int tryY1 = Integer.parseInt(this.y1);
            int tryY2 = Integer.parseInt(this.y2);
            returnValue = String.valueOf(Math.min(tryY1, tryY2));

        } catch (Exception e) {

            // do nothing, will fail if using *

        }
        return returnValue;

    }

    public String getMinZ() {

        String returnValue = this.z1;
        try {

            int tryZ1 = Integer.parseInt(this.z1);
            int tryZ2 = Integer.parseInt(this.z2);
            returnValue = String.valueOf(Math.min(tryZ1, tryZ2));

        } catch (Exception e) {

            // do nothing, will fail if using *

        }
        return returnValue;

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
