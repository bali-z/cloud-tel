package com.ruoyi.rtc.meeting;

import java.io.Serializable;

public enum SignalTypeEnum implements Serializable {
    CONNECT_SUCCESS("CONNECT_SUCCESS", "CONNECT_SUCCESS"),
    PING("PING", "PING"),
    PONG("PONG", "PONG"),
    JOIN("JOIN", "JOIN"),
    JOIN_CONFIRM("JOIN_CONFIRM", "JOIN_CONFIRM"),
    JOIN_RESOLVE("JOIN_RESOLVE", "JOIN_RESOLVE"),
    JOIN_REJECT("JOIN_REJECT", "JOIN_REJECT"),
    ENTER("ENTER", "ENTER"),
    HANG_UP("HANG_UP", "HANG_UP"),
    CANDIDATE("CANDIDATE", "CANDIDATE"),
    OFFER("OFFER", "OFFER"),
    ANSWER("ANSWER", "ANSWER"),
    ;


    private String code;
    private String desc;

    SignalTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
