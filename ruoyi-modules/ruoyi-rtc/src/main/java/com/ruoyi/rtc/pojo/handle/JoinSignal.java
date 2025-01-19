package com.ruoyi.rtc.pojo.handle;

import com.ruoyi.rtc.pojo.SignalBase;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class JoinSignal extends SignalBase {
    /**
     *  会议密码
     */
    private String password;
    /**
     *  自己的sessionId
     */
    private String selfSessionId;
}
