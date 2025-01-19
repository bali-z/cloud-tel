package com.ruoyi.rtc.pojo;

/**
 * @author dz
 */
public enum SignalType {

    CONNECT_SUCCESS("CONNECT_SUCCESS", "CONNECT_SUCCESS"),
    PING("PING","PING"),
    PONG("PONG", "PONG"),
    JOIN("JOIN","APPLY JOIN MEETING"),
    OFFER("OFFER","OFFER"),
    ANSWER("ANSWER", "ANSWER"),
    CANDIDATE("CANDIDATE","CANDIDATE"),
    JOIN_CONFIRM("JOIN_CONFIRM", "JOIN MEETING CONFIRM"),
    JOIN_RESOLVE("JOIN_RESOLVE","The initiator agrees to join the group chat!"),
    JOIN_REJECT("JOIN_REJECT", "The initiator did not respond or refused!"),
    ENTER("ENTER", "进入会议"),
    HANGUP("HANGUP", "挂断"),
    HANGUP_SUCCESS("HANGUP_RESPONSE", "挂断成功");

    private final String signal;
    private final String content;

    SignalType(String signal, String content){
        this.signal = signal;
        this.content = content;
    }

    public String getSignal() {
        return signal;
    }

    public String getContent() {
        return content;
    }
}
