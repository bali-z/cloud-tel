package com.ruoyi.rtc.pojo.forward;

import com.ruoyi.rtc.pojo.SignalBase;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.apache.catalina.User;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class EnterInfo extends SignalBase {
    private Long userId;
    private String name;
    private String avatar;
}
