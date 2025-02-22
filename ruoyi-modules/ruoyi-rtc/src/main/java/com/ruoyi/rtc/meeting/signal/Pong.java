package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("ALL")
@Data
public class Pong extends MeetingSignal implements Serializable {
    public Pong() {
        super();
        super.setCode(SignalTypeEnum.PONG);
    }
}
