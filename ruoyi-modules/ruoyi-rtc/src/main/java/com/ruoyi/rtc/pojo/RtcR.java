package com.ruoyi.rtc.pojo;

import com.ruoyi.common.core.domain.R;
import lombok.Data;

/**
 * @author dz
 */
@Data
public class RtcR extends R {
    /**
     *  信令
     */
    private SignalType signal;

    public static RtcR instance(int code,SignalType signal, String msg, Object data) {
        RtcR rtcR = new RtcR();
        rtcR.setCode(code);
        rtcR.setSignal(signal);
        rtcR.setMsg(msg);
        rtcR.setData(data);
        return rtcR;
    }
}
