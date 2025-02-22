package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("ALL")
@Data
public class HangUp extends MeetingSignal implements Serializable {
    /**
     *  离会人姓名
     */
    private String name;

    /**
     *  离会人头像
     */
    private String avatar;

    public HangUp(){
        super();
        super.setCode(SignalTypeEnum.HANG_UP);
    }
}
