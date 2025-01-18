package com.ruoyi.rtc.pojo;

import java.io.Serializable;

/**
 * @author dz
 */
public class JoinConfirmMessage extends BaseMessage implements Serializable {
    /**
     *  发送给会议管理员的 申请加入会议人员用户名
     */
    private String fromUserName;
    /**
     *  发送给会议管理员的 申请加入会议人员头像
     */
    private String fromUserAvatar;

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserAvatar() {
        return fromUserAvatar;
    }

    public void setFromUserAvatar(String fromUserAvatar) {
        this.fromUserAvatar = fromUserAvatar;
    }
}
