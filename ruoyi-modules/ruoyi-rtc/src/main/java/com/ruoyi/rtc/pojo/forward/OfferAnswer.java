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
    private Description description;

    @Data
    public static class Description {
        private String sdp;
        private String type;
    }
}
