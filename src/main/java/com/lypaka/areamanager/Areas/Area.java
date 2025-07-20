package com.lypaka.areamanager.Areas;

public class Area {

    private final String name;
    private final String displayName;
    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;
    private final String worldName;
    private final String enterTitle;
    private final String enterSubtitle;
    private final String leaveTitle;
    private final String leaveSubtitle;
    private final String plainName;
    private final boolean killForSwimming;
    private final boolean teleportForSwimming;
    private final AreaPermissions permissions;
    private final int priority;
    private final int radius;
    private final int underground;

    public Area (String name, String displayName, int x1, int y1, int z1, int x2, int y2, int z2, String worldName, String enterTitle, String enterSubtitle, String leaveTitle,
                 String leaveSubtitle, String plainName, boolean killForSwimming, boolean teleportForSwimming, AreaPermissions permissions, int priority, int radius, int underground) {

        this.name = name;
        this.displayName = displayName;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.worldName = worldName;
        this.enterTitle = enterTitle;
        this.enterSubtitle = enterSubtitle;
        this.leaveTitle = leaveTitle;
        this.leaveSubtitle = leaveSubtitle;
        this.plainName = plainName;
        this.killForSwimming = killForSwimming;
        this.teleportForSwimming = teleportForSwimming;
        this.permissions = permissions;
        this.priority = priority;
        this.radius = radius;
        this.underground = underground;

    }

    public String getName() {

        return this.name;

    }

    public String getDisplayName() {

        return this.displayName;

    }

    public int getMaxX() {

        return Math.max(this.x1, this.x2);

    }

    public int getMaxY() {

        return Math.max(this.y1, this.y2);

    }

    public int getMaxZ() {

        return Math.max(this.z1, this.z2);

    }

    public int getMinX() {

        return Math.min(this.x1, this.x2);

    }

    public int getMinY() {

        return Math.min(this.y1, this.y2);

    }

    public int getMinZ() {

        return Math.min(this.z1, this.z2);

    }

    public String getWorldName() {

        return this.worldName;

    }

    public String getEnterTitle() {

        return this.enterTitle;

    }

    public String getEnterSubtitle() {

        return this.enterSubtitle;

    }

    public String getLeaveTitle() {

        return this.leaveTitle;

    }

    public String getLeaveSubtitle() {

        return this.leaveSubtitle;

    }

    public String getPlainName() {

        return this.plainName;

    }

    public boolean killsForSwimming() {

        return this.killForSwimming;

    }

    public boolean teleportsForSwimming() {

        return this.teleportForSwimming;

    }

    public AreaPermissions getPermissions() {

        return this.permissions;

    }

    public int getPriority() {

        return this.priority;

    }

    public int getRadius() {

        return this.radius;

    }

    public int getUnderground() {

        return this.underground;

    }

}
