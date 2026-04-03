package de.satsuya.ruinsCore.core.permission;

public enum PermissionNode {
    COMMAND_PING("ruinscore.command.ping"),
    COMMAND_STAFF_ALERT("ruinscore.command.staffalert"),
    COMMAND_MONEY("ruinscore.command.money"),
    COMMAND_MONEY_ADMIN("ruinscore.command.money.admin"),
    COMMAND_PAY("ruinscore.command.pay"),
    COMMAND_REQUEST("ruinscore.command.request"),
    COMMAND_SUDO("ruinscore.command.sudo"),
    COMMAND_GAMEMODE("ruinscore.command.gamemode"),
    COMMAND_FLY("ruinscore.command.fly"),
    COMMAND_SPEED("ruinscore.command.speed"),
    COMMAND_FLYSPEED("ruinscore.command.flyspeed"),
    COMMAND_RESET_SPEEDS("ruinscore.command.resetallspeeds"),
    COMMAND_VANISH("ruinscore.command.vanish"),
    COMMAND_FIREBALL("ruinscore.command.fireball"),
    COMMAND_SMITE("ruinscore.command.smite"),
    COMMAND_MSG("ruinscore.command.msg"),
    COMMAND_MSG_SPY("ruinscore.command.msg.spy"),
    COMMAND_SUPPORT_MODE("ruinscore.command.supportmode"),
    COMMAND_BROADCAST("ruinscore.command.broadcast"),
    COMMAND_FREEZE("ruinscore.command.freeze"),
    COMMAND_INVSEE("ruinscore.command.invsee"),
    COMMAND_ENDERSEE("ruinscore.command.endersee"),
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

