package com.ruoyi.rtc.pojo;

import java.io.Serializable;

/**
 * join 信令
 *
 * @author dz
 */
public class JoinMessage extends BaseMessage implements Serializable {
    /**
     *  申请入会用户本系统登录后的 token
     */
    private String applyUserToken;

    /**
     * 申请入会用户sessionId
     */
    private String applySessionId;

    /**
     * 会议票据信息
     */
    private String ticket;


    public String getApplyUserToken() {
        return applyUserToken;
    }

    public void setApplyUserToken(String applyUserToken) {
        this.applyUserToken = applyUserToken;
    }

    public String getApplySessionId() {
        return applySessionId;
    }

    public void setApplySessionId(String applySessionId) {
        this.applySessionId = applySessionId;
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
