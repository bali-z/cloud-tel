package com.ruoyi.rtc.pojo.forward;

import com.ruoyi.rtc.pojo.SignalBase;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class JoinRejectInfo extends SignalBase {
    private String name;
    private String avatar;
}
