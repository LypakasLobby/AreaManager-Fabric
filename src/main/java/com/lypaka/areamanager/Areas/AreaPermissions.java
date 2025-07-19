package com.lypaka.areamanager.Areas;

import java.util.List;

public class AreaPermissions {

    private final String enterMessage;
    private final List<String> enterPermissions;
    private final String enterTeleportLocation;
    private final String leaveMessage;
    private final List<String> leavePermissions;
    private final String leaveTeleportLocation;

    public AreaPermissions (String enterMessage, List<String> enterPermissions, String enterTeleportLocation, String leaveMessage, List<String> leavePermissions, String leaveTeleportLocation) {

        this.enterMessage = enterMessage;
        this.enterPermissions = enterPermissions;
        this.enterTeleportLocation = enterTeleportLocation;
        this.leaveMessage = leaveMessage;
        this.leavePermissions = leavePermissions;
        this.leaveTeleportLocation = leaveTeleportLocation;

    }

    public String getEnterMessage() {

        return this.enterMessage;

    }

    public List<String> getEnterPermissions() {

        return this.enterPermissions;

    }

    public String getEnterTeleportLocation() {

        return this.enterTeleportLocation;

    }

    public String getLeaveMessage() {

        return this.leaveMessage;

    }

    public List<String> getLeavePermissions() {

        return this.leavePermissions;

    }

    public String getLeaveTeleportLocation() {

        return this.leaveTeleportLocation;

    }

}
