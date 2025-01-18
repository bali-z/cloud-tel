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
     * 发送信令用户 token
     */
    private String sendUser;

    /**
     * 信令所属会议
     */
    private String meetingId;

    /**
     * 会议票据 (确认是否可以参加本场会议)
     */
    private String ticket;

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
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
