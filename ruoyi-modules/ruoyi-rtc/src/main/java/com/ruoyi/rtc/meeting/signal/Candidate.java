package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 *  网络信息交换信令
 */
@Data
@SuppressWarnings("ALL")
public class Candidate extends MeetingSignal implements Serializable {
    private CandidateInfo candidate;

    @Data
    public static class CandidateInfo implements Serializable{
        private String candidate;
        private Integer sdpMLineIndex;
        private String sdpMid;
        private String usernameFragment;
    }

    public Candidate() {
        super();
        super.setCode(SignalTypeEnum.CANDIDATE);
    }
}
