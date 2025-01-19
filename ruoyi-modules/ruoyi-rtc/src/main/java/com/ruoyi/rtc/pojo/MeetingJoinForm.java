package com.ruoyi.rtc.pojo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author dz
 */
public class MeetingJoinForm {
    /**
     *  会议 id
     */
    @NotEmpty(message = "会议meetingId不能为空!")
    private String meetingId;
    /**
     *  会议密码
     */
    private String password;

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
