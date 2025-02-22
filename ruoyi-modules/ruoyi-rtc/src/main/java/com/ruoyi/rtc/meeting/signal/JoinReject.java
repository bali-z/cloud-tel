package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("ALL")
@Data
public class JoinReject extends MeetingSignal implements Serializable {
    public JoinReject(){
        super();
        super.setCode(SignalTypeEnum.JOIN_REJECT);
    }
}
