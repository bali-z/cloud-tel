package com.ruoyi.rtc.pojo;


import java.io.Serializable;

/**
 * 信令信息基本类 封装
 *
 * @author dz
 */
public class BaseMessage implements Serializable {
    /**
     * 信令
     */
    private SignalType signal;

    /**
     * 发送信令连接 sessionId
     */
    private String sendSessionId;

    /**
     *  接收信令连接 sessionId
     */
    private String recvSessionId;

    /**
     * 信令所属会议
     */
    private String meetingId;

    /**
     * 会议票据 (确认是否可以参加本场会议)
     */
    private String ticket;


    public String getRecvSessionId() {
        return recvSessionId;
    }

    public void setRecvSessionId(String recvSessionId) {
        this.recvSessionId = recvSessionId;
    }

    public String getSendSessionId() {
        return sendSessionId;
    }

    public void setSendSessionId(String sendSessionId) {
        this.sendSessionId = sendSessionId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public SignalType getSignal() {
        return signal;
    }

    public void setSignal(SignalType signal) {
        this.signal = signal;
    }
}
