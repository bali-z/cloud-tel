package com.ruoyi.rtc.pojo.forward;

import com.ruoyi.rtc.pojo.SignalBase;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author dz
 */
@SuppressWarnings("ALL")
@Data
@Accessors(chain = true)
public class HangUpInfo extends SignalBase {
    private String name;
    private String avatar;
}
