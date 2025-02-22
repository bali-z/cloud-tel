package com.ruoyi.rtc.business;


import lombok.Data;

import java.io.Serializable;

@Data
public class BaseMessage implements Serializable {
    /**
     * 生成的 sessionId，连接建立完成后分配
     */
    public static final String CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION = "CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION";
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
