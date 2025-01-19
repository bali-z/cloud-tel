package com.ruoyi.rtc.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class ResponseR {
    private Integer code;
    private SignalType signal;
    private Object data;
}
