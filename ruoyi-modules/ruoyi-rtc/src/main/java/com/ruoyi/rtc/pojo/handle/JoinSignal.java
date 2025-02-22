package com.ruoyi.rtc.pojo.handle;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.StringUtils;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.rtc.domain.SysMeeting;
import com.ruoyi.rtc.handler.SignalWebSocketHandler;
import com.ruoyi.rtc.pojo.*;
import com.ruoyi.rtc.pojo.forward.JoinConfirmInfo;
import com.ruoyi.rtc.service.ISysMeetingService;
import com.ruoyi.rtc.util.MeetingUtil;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysUser;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import static com.ruoyi.rtc.handler.SignalWebSocketHandler.CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION;

/**
 * @author dz
 */
@SuppressWarnings("ALL")
@Data
@Accessors(chain = true)
public class JoinSignal extends SignalBase {
    private static final Logger log = LoggerFactory.getLogger(JoinSignal.class);

    /**
     * 处理 Join 信令
     * 携带 meetingId,ticket
     * 为了确认是否能够进入会议：
     * 能够进入会议能就给当前session发一个JoinConfirm信令
     *
     * @param sysMeetingService
     * @param redisService
     * @param remoteUserService
     * @param data
     * @param selfSession
     */
    public static void dealJoin(ISysMeetingService sysMeetingService, RedisService redisService, RemoteUserService remoteUserService, JSONObject data, WebSocketSession selfSession) {
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
            SignalWebSocketHandler.sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.FAIL, "会议不存在或已取消!", "会议不存在或已取消!")), sessionId);
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        // 会议未开始或者已经结束
        if (meeting.getStartTime().getTime() > currentTimeMillis || meeting.getEndTime().getTime() < currentTimeMillis) {
            log.debug("The meeting has not started or has already ended!");
            SignalWebSocketHandler.sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.FAIL, "会议未开始在或已结束!", "会议未开始在或已结束!")), sessionId);
            return;
        }

        // 如果是会议发起者，不用校验票据，JOIN_RESOLVE
        if (meeting.getUserId().equals(currentUserId)) {
            String newTicket = null;
            // 校验票据是否失效，如果失效则再下发一个新的票据
            if (!SignalBase.verifyTicket(ticket, currentUserId, meetingId, meeting.getUserId())) {
                newTicket = SignalBase.createTicket(meeting, currentUserId);
            }
            SignalWebSocketHandler.sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.JOIN_RESOLVE, "", newTicket)), sessionId);
            return;
        }
        // 当前信令不是会议发起者发送的
        // 校验票据
        if (SignalBase.verifyTicket(ticket, currentUserId, meetingId, meeting.getUserId())) {
            SignalWebSocketHandler.sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.JOIN_RESOLVE, "", "")), sessionId);
            return;
        }
        // 票据不存在/票据不存在,交由会议发起者管理
        // 票据不正确，需要给管理员发送JOIN_CONFIRM 信令
        MeetingInfo meetingInfo = MeetingUtil.getMeetingInfo(redisService, meetingId);
        List<MeetingMember> meetingMembers = meetingInfo.getMeetingMembers();
        MeetingMember adminMember = null;
        for (MeetingMember member : meetingMembers) {
            if (member.getUserId().equals(meeting.getUserId())) {
                adminMember = member;
                break;
            }
        }
        // 管理员未进入会议
        if (adminMember == null) {
            // 发送拒绝信令
            SignalWebSocketHandler.sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.JOIN_REJECT, "会议发起还未进入会议!", "会议发起还未进入会议!")), sessionId);
            return;
        }

        // 会议发起人在线
        // 给会议发起者发送JOIN_CONFIRM
        JoinConfirmInfo joinConfirmInfo = new JoinConfirmInfo();
        joinConfirmInfo.setMeetingId(meetingId);
        joinConfirmInfo.setSourceSessionId(sessionId);
        joinConfirmInfo.setSourceUserId(currentUserId);
        joinConfirmInfo.setTargetSessionId(adminMember.getSessionId());
        joinConfirmInfo.setUserId(currentUserId);
        R<SysUser> userInfoById = remoteUserService.getUserInfoById(currentUserId, SecurityConstants.INNER);
        joinConfirmInfo.setName(userInfoById.getData().getUserName());
        joinConfirmInfo.setAvatar(userInfoById.getData().getAvatar());
        // 向会议发起人发送加入会议确认请求
        SignalWebSocketHandler.sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.JOIN_CONFIRM, "", joinConfirmInfo)), adminMember.getSessionId());

    }
}
