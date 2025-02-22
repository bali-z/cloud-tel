package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("ALL")
@Data
public class Enter extends MeetingSignal implements Serializable {
    /**
     *  参会人姓名
     */
    private String name;

    /**
     *  参会人头像
     */
    private String avatar;

    public Enter(){
        super();
        super.setCode(SignalTypeEnum.ENTER);
    }
}
