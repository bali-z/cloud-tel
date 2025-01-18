package com.ruoyi.rtc.pojo;

/**
 * @author dz
 */
public enum SignalType {
    /**
     *  连接成功信令
     */
    CONNECT_SUCCESS("CONNECT_SUCCESS", "CONNECT_SUCCESS"),

    /**
     *  心跳信令
     */
    PING("PING","PING"),

    /**
     *  心跳响应信令
     */
    PONG("PONG", "PONG"),

    /**
     *  offer信令
     */
    OFFER("OFFER","OFFER"),

    /**
     *  answer信令
     */
    ANSWER("ANSWER", "ANSWER"),

    /**
     *  candidate信令
     */
    CANDIDATE("CANDIDATE","CANDIDATE"),

    /**
     *  加入会议确认信令
     */
    JOIN_CONFIRM("JOIN_CONFIRM", "JOIN MEETING CONFIRM"),
    /**
     *  加入会议申请信令
     */
    JOIN("JOIN","APPLY JOIN MEETING"),
    /**
     *  加入会议同意信令
     */
    JOIN_RESOLVE("JOIN_RESOLVE","The initiator agrees to join the group chat!"),
    /**
     *  加入会议拒绝信令
     */
    JOIN_REJECT("JOIN_REJECT", "The initiator did not respond or refused!"),
    /**
     *  进入会议信令
     */
    ENTER("ENTER", "进入会议"),
    /**
     *  挂断信令
     */
    HANGUP("HANGUP", "挂断"),
    /**
     *  挂断成功信令
     */
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
