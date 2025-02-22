package com.ruoyi.rtc.pojo.forward;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.StringUtils;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.rtc.domain.SysMeeting;
import com.ruoyi.rtc.handler.SignalWebSocketHandler;
import com.ruoyi.rtc.pojo.MeetingMember;
import com.ruoyi.rtc.pojo.RtcR;
import com.ruoyi.rtc.pojo.SignalBase;
import com.ruoyi.rtc.pojo.SignalType;
import com.ruoyi.rtc.service.ISysMeetingService;
import com.ruoyi.rtc.util.MeetingUtil;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysUser;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import static com.ruoyi.rtc.handler.SignalWebSocketHandler.CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION;

/**
 * @author dz
 */
@SuppressWarnings("ALL")
@Data
@Accessors(chain = true)
public class EnterInfo extends SignalBase {
    private static final Logger log = LoggerFactory.getLogger(EnterInfo.class);
    private Long userId;
    private String name;
    private String avatar;

    /**
     * 处理Enter信令
     *
     * @param sysMeetingService
     * @param redisService
     * @param remoteUserService
     * @param data
     * @param selfSession
     */
    public static void dealEnter(ISysMeetingService sysMeetingService, RedisService redisService, RemoteUserService remoteUserService, JSONObject data, WebSocketSession selfSession) {
        // 1.校验票据是否有效
        EnterInfo enterInfo = JSONObject.toJavaObject(data, EnterInfo.class);
        String meetingId = enterInfo.getMeetingId();
        String ticket = enterInfo.getTicket();
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

        // 校验票据
        if (SignalBase.verifyTicket(ticket, currentUserId, meetingId, meeting.getUserId())) {
            // 将新成员信息加入到会议中
            // 1.获取新成员的完整信息
            R<SysUser> userInfoById = remoteUserService.getUserInfoById(currentUserId, SecurityConstants.INNER);
            // 2.构建member
            MeetingMember member = new MeetingMember();
            member.setUserId(currentUserId);
            member.setSessionId(sessionId);
            member.setName(userInfoById.getData().getUserName());
            member.setTicket(ticket);
            member.setAvatar(userInfoById.getData().getAvatar());
            member.setPermission("1|1|1");
            // 将新成员加入会议并告知其他成员
            MeetingUtil.addMemberToMeeting(redisService, meetingId, member);
        }

    }
}
