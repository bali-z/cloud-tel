package com.ruoyi.rtc.business;


import lombok.Data;

import java.io.Serializable;

@Data
public class BaseMessage implements Serializable {

    /**
     * 业务类型
     */
    private BusinessTypeEnum businessType;
    /**
     * 源头sessionId
     */
    private String sourceSessionId;
    /**
     * 目标sessionId
     */
    private String targetSessionId;
}
