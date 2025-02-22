package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("ALL")
@Data
public class Ping extends MeetingSignal implements Serializable {
    public Ping() {
        super();
        super.setCode(SignalTypeEnum.PING);
    }
}
