package com.ruoyi.rtc.pojo.forward;

import com.ruoyi.rtc.pojo.SignalBase;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class ConnectSuccessR extends SignalBase {
    /**
     * websocket 连接后获取到的 sessionId
     */
    private String sessionId;
}
