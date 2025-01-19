package com.ruoyi.rtc.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class MeetingJoinForm {
    /**
     * 会议 id
     */
    @NotEmpty(message = "会议meetingId不能为空!")
    private String meetingId;
    /**
     * 会议密码
     */
    private String password;

}
