package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 *  音视频媒体信交换的Offer信令
 */
@SuppressWarnings("ALL")
@Data
public class Offer extends MeetingSignal implements Serializable {
    private Description description;

    @Data
    public static class Description {
        private String sdp;
        private String type;
    }

    public Offer(){
        super();
        super.setCode(SignalTypeEnum.OFFER);
    }
}
