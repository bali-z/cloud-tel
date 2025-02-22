package com.ruoyi.rtc.meeting.signal;

import com.ruoyi.rtc.meeting.SignalTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author dz
 */
@Data
public class ConnectSuccess extends MeetingSignal implements Serializable {
    /**
     *  返回连接成功后的 sessionId
     */
    private String sessionId;

    public ConnectSuccess() {
        super();
        super.setCode(SignalTypeEnum.CONNECT_SUCCESS);
    }
    public ConnectSuccess(String sessionId) {
        super();
        super.setCode(SignalTypeEnum.CONNECT_SUCCESS);
        this.sessionId = sessionId;
    }
}
