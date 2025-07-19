package com.lypaka.areamanager.Wand;

public class WandPOS {

    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public WandPOS (String world, int x, int y, int z) {

        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;

    }

    public String getWorld() {

        return this.world;

    }

    public int getX() {

        return this.x;

    }

    public int getY() {

        return this.y;

    }

    public int getZ() {

        return this.z;

    }

}
