package com.ruoyi.rtc.business;

import java.io.Serializable;

/**
 *  业务类型枚举
 */
public enum BusinessTypeEnum implements Serializable {
    MEETING("MEETING", "会议业务");

    private String type;
    private String desc;

    BusinessTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public String getType() {
        return type;
    }
    public String getDesc() {
        return desc;
    }
}
