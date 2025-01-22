package com.ruoyi.rtc.pojo;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class SignalBase {
    /**
     *  信令发送方
     */
    private String sourceSessionId;
    /**
     *  信令接受方
     */
    private String targetSessionId;

    /**
     *  会议id
     */
    private String meetingId;

    /**
     *  票据 (meetingId,meetingOwnerUserId,meetingMemberUserId) -> JWT -> ticket
     */
    private String ticket;

}
