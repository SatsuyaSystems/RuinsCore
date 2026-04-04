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
    CHAT_BYPASS("ruinscore.chat.bypass"),
    COMMAND_WARN("ruinscore.command.warn"),
    COMMAND_WARNS("ruinscore.command.warns"),
    COMMAND_SIZE("ruinscore.command.size"),
    COMMAND_SIZE_OTHER("ruinscore.command.size.other"),
    JOB_MANAGE("ruinscore.job.manage"),
    JOB_GUI("ruinscore.job.gui"),
    AUCTION_USE("ruinscore.auction.use"),
    STAFF_ALERTS_SEND("ruinscore.staff.alert.send"),
    STAFF_ALERTS_RECEIVE("ruinscore.staff.alert.receive"),
    MARRY("ruinscore.command.marry"),
    COMMAND_PLAYTIME("ruinscore.command.playtime"),
    COMMAND_PLAYTIME_OTHER("ruinscore.command.playtime.other"),
    COMMAND_PENTAGRAM("ruinscore.command.pentagram"),
    COMMAND_TURLOCK_ADMIN("ruinscore.command.turlock.admin"),
    REPORT_USE("ruinscore.command.report.use"),
    REPORT_VIEW("ruinscore.command.report.view");

    private final String node;

    PermissionNode(String node) {
        this.node = node;
    }

    public String getNode() {
        return node;
    }
}

