package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

@Data
@SuppressWarnings("ALL")
public class JoinResolve extends MeetingSignal implements Serializable {
    public JoinResolve() {
        super();
        super.setCode(SignalTypeEnum.JOIN_RESOLVE);
    }
}
