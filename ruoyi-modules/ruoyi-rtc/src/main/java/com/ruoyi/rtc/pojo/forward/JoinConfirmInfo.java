package com.ruoyi.rtc.pojo.forward;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class JoinConfirmInfo {
    /**
     *  用户姓名
     */
    private String name;
    /**
     *  用户头像
     */
    private String avatar;
}
