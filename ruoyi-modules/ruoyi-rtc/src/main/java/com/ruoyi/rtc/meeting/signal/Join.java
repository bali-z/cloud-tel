package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("ALL")
@Data
public class Join extends MeetingSignal implements Serializable {
    public Join(){
        super();
        super.setCode(SignalTypeEnum.JOIN);
    }
}
