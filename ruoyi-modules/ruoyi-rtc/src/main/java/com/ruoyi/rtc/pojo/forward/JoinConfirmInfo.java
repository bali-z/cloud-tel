package com.ruoyi.rtc.pojo.forward;

import com.ruoyi.rtc.pojo.SignalBase;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class JoinConfirmInfo extends SignalBase {
    /**
     *  用户id
     */
    private Long userId;

    /**
     *  用户姓名
     */
    private String name;
    /**
     *  用户头像
     */
    private String avatar;
}
