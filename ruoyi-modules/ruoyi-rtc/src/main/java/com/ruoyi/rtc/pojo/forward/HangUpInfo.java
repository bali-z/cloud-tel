package com.ruoyi.rtc.pojo.forward;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.rtc.handler.SignalWebSocketHandler;
import com.ruoyi.rtc.pojo.RtcR;
import com.ruoyi.rtc.pojo.SignalBase;
import com.ruoyi.rtc.pojo.SignalType;
import com.ruoyi.rtc.service.ISysMeetingService;
import com.ruoyi.rtc.util.MeetingUtil;
import com.ruoyi.system.api.RemoteUserService;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.socket.WebSocketSession;

import static com.ruoyi.rtc.handler.SignalWebSocketHandler.CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION;

/**
 * @author dz
 */
@SuppressWarnings("ALL")
@Data
@Accessors(chain = true)
public class HangUpInfo extends SignalBase {
    private String name;
    private String avatar;

    /**
     *  挂断逻辑
     * @param sysMeetingService
     * @param redisService
     * @param remoteUserService
     * @param data
     * @param session
     */
    public static void dealHangUp(ISysMeetingService sysMeetingService, RedisService redisService, RemoteUserService remoteUserService, JSONObject data, WebSocketSession selfSession) {
        String sessionId = (String) selfSession.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION);
        HangUpInfo hangUpInfo = JSONObject.toJavaObject(data, HangUpInfo.class);
        MeetingUtil.hangUpMemberFromMeeting(redisService,hangUpInfo.getMeetingId(),hangUpInfo.getSourceUserId());
        // 给挂断会议这个比发一个挂断成功
        SignalWebSocketHandler.sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.HANGUP_SUCCESS, "HANGUP_SUCCESS!", "HANGUP_SUCCESS!")),sessionId);
    }
}
