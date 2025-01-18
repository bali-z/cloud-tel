package com.ruoyi.rtc.pojo;

import java.io.Serializable;

/**
 * join 信令
 *
 * @author dz
 */
public class JoinMessage extends BaseMessage implements Serializable {
    /**
     * 申请入会用户token信息
     */
    private String applyUser;

    /**
     * 会议票据信息
     */
    private String ticket;

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser;
    }

    @Override
    public String getTicket() {
        return ticket;
    }

    @Override
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
