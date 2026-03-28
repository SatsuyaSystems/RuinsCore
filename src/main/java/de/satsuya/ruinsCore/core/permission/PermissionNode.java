package de.satsuya.ruinsCore.core.permission;

public enum PermissionNode {
    COMMAND_PING("ruinscore.command.ping"),
    COMMAND_STAFF_ALERT("ruinscore.command.staffalert"),
    JOB_MANAGE("ruinscore.job.manage"),
    JOB_GUI("ruinscore.job.gui"),
    STAFF_ALERTS_SEND("ruinscore.staff.alert.send"),
    STAFF_ALERTS_RECEIVE("ruinscore.staff.alert.receive");

    private final String node;

    PermissionNode(String node) {
        this.node = node;
    }

    public String getNode() {
        return node;
    }
}

