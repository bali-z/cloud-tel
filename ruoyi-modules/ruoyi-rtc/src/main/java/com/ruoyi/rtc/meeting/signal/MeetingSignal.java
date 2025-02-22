package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.business.BaseMessage;
import com.ruoyi.rtc.business.BusinessTypeEnum;
import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("ALL")
@Data
public class MeetingSignal extends BaseMessage implements Serializable {

    public MeetingSignal() {
        super();
        super.setBusinessType(BusinessTypeEnum.MEETING);
    }

    /**
     * 信令类型
     */
    private SignalTypeEnum code;
    /**
     * 会议号
     */
    private String meetingId;
    /**
     * 会议票据
     */
    private String ticket;
    /**
     * 信令发送者userId
     */
    private Long sourceUserId;
    /**
     * 信令接受者userId
     */
    private Long targetUserId;
}
