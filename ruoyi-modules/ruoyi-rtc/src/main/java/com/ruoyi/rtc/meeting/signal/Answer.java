package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("ALL")
@Data
public class Answer extends MeetingSignal implements Serializable {
    private Offer.Description description;

    public Answer() {
        super();
        this.setCode(SignalTypeEnum.ANSWER);
    }
}
