package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

@Data
@SuppressWarnings("ALL")
public class JoinResolve extends MeetingSignal implements Serializable {

    private String name;
    private String avatar;
    private Long userId;


    public JoinResolve() {
        super();
        super.setCode(SignalTypeEnum.JOIN_RESOLVE);
    }
}
