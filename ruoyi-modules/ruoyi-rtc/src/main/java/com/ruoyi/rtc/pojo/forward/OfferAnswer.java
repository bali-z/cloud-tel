package com.ruoyi.rtc.pojo.forward;

import com.ruoyi.rtc.pojo.SignalBase;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class OfferAnswer extends SignalBase {
    /**
     *  设备音视频信息
     */
    private String sdp;
    /**
     *  类型 offer / answer
     */
    private String type;
}
