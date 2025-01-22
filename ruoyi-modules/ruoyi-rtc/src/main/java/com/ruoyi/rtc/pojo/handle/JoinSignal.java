package com.ruoyi.rtc.pojo.handle;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.StringUtils;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.rtc.domain.SysMeeting;
import com.ruoyi.rtc.handler.SignalWebSocketHandler;
import com.ruoyi.rtc.pojo.ResponseR;
import com.ruoyi.rtc.pojo.SignalBase;
import com.ruoyi.rtc.pojo.SignalType;
import com.ruoyi.rtc.service.ISysMeetingService;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import static com.ruoyi.rtc.handler.SignalWebSocketHandler.CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION;
import static com.ruoyi.rtc.pojo.Constants.*;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class JoinSignal extends SignalBase {
    private static final Logger log = LoggerFactory.getLogger(JoinSignal.class);

    /**
     * 处理 Join 信令
     * 携带 meetingId,ticket
     *
     * @param sysMeetingService
     * @param redisService
     * @param data
     * @param selfSession
     */
    public static void dealJoin(ISysMeetingService sysMeetingService, RedisService redisService, JSONObject data, WebSocketSession selfSession) {
        JoinSignal joinSignal = JSONObject.toJavaObject(data, JoinSignal.class);
        String meetingId = joinSignal.getMeetingId();
        String ticket = joinSignal.getTicket();
        String sessionId = (String) selfSession.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION);
        Long currentUserId = Long.parseLong((String) selfSession.getAttributes().get(SecurityConstants.DETAILS_USER_ID));
        // 没携带任何东西,直接不处理
        if (StringUtils.isEmpty(meetingId) && StringUtils.isEmpty(ticket)) {
            log.error("Join signal incomplete signaling,message:{}", data);
            return;
        }
        // 获取会议信息
        SysMeeting meeting = sysMeetingService.getMeetingByMeetingId(meetingId);
        if (null == meeting) {
            log.debug("The meeting does not exist or has been cancelled!");
            SignalWebSocketHandler.sendMessage(JSONObject.toJSONString(new ResponseR().setSignal(SignalType.FAIL).setCode(200).setData("会议不存在或已取消!")), sessionId);
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        // 会议未开始或者已经结束
        if (meeting.getStartTime().getTime() > currentTimeMillis || meeting.getEndTime().getTime() < currentTimeMillis) {
            log.debug("The meeting has not started or has already ended!");
            SignalWebSocketHandler.sendMessage(JSONObject.toJSONString(new ResponseR().setSignal(SignalType.FAIL).setCode(200).setData("会议未开始在或已结束!")), sessionId);
            return;
        }
        // todo
        // 如果是会议发起者，不用校验票据，直接允许进入
        if (meeting.getUserId().equals(currentUserId)) {

        }

        // 校验票据
        if (StringUtils.isNotEmpty(ticket)) {
            Claims claims = JwtUtils.parseToken(ticket);
            String meetingIdInTicket = (String) claims.get(MEETING_TICKET_ITEM_MEETING_ID);
            Long userIdInTicket = (Long) claims.get(MEETING_TICKET_ITEM_MEETING_OWNER_USER_ID);
            Long memberIdInTicket = (Long) claims.get(MEETING_TICKET_ITEM_MEETING_MEMBER_USER_ID);

        }


    }
}
