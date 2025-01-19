package com.ruoyi.rtc.pojo.handle;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.rtc.pojo.MeetingInfo;
import com.ruoyi.rtc.pojo.ResponseR;
import com.ruoyi.rtc.pojo.SignalBase;
import com.ruoyi.rtc.pojo.SignalType;
import com.ruoyi.rtc.pojo.forward.EnterInfo;
import com.ruoyi.rtc.pojo.forward.JoinRejectInfo;
import com.ruoyi.rtc.websocket.RtcWebSocket;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ruoyi.rtc.pojo.Constants.ONLINE_MEETING_PREFIX_KEY;

/**
 * @author dz
 */
@Data
@Accessors(chain = true)
public class JoinSignal extends SignalBase {
    private static final Logger log = LoggerFactory.getLogger(JoinSignal.class);
    /**
     * 自己的sessionId
     */
    private String selfSessionId;

    /**
     * 处理 加入申请信令
     *
     * @param rtcWebSocket
     * @param data
     */
    public static void dealJoin(RedisService redisService, RtcWebSocket rtcWebSocket, JSONObject data) {
        if (data == null) {
            return;
        }
        try {
            JoinSignal joinSignal = JSONObject.toJavaObject(data, JoinSignal.class);
            if (joinSignal == null) {
                return;
            }
            String meetingId = joinSignal.getMeetingId();
            String ticket = joinSignal.getTicket();
            Long currentUserId = rtcWebSocket.getCurrentUserId();
            MeetingInfo meetingInfo = redisService.getCacheObject(ONLINE_MEETING_PREFIX_KEY + meetingId);

            if (meetingInfo == null) {
                // 会议不存在 发送拒绝信令
                log.error("online meetingId:{} is not exist", meetingId);
                SignalBase reject = new JoinRejectInfo().setMeetingId(meetingId).setSourceSessionId(rtcWebSocket.getCurrentUserSessionId());
                ResponseR responseR = new ResponseR().setSignal(SignalType.JOIN_REJECT).setData(reject).setCode(200);
                RtcWebSocket.sendMessage(rtcWebSocket.getCurrentUserSessionId(), JSONObject.toJSONString(responseR));
                return;
            }

            // 判断当前用户是否是会议所有者
            if (currentUserId.equals(meetingInfo.getMeetingOwnerUserId())) {
                // 可以直接发送enter
                log.info("currentUserId:{} is meetingOwnerUserId:{}", currentUserId, meetingInfo.getMeetingOwnerUserId());
                EnterInfo enter = (EnterInfo) new EnterInfo().setMeetingId(meetingId).setSourceSessionId(rtcWebSocket.getCurrentUserSessionId());
                // 给其他所有人发enter信令，通知
                List<MeetingInfo.MeetingMember> meetingMembers = meetingInfo.getMeetingMembers();
                if (meetingMembers != null) {
                    meetingMembers.forEach(meetingMember -> {
                        RtcWebSocket.sendMessage(meetingMember.getSessionId(),
                                JSONObject.toJSONString(new ResponseR().setData(enter).setSignal(SignalType.ENTER).setCode(200)));
                    });
                } else {
                    meetingMembers = new ArrayList<>();
                    meetingInfo.setMeetingMembers(meetingMembers);
                }
                meetingMembers.add(new MeetingInfo.MeetingMember()
                        .setPermission("1|1|1").setSessionId(rtcWebSocket.getCurrentUserSessionId())
                        .setUserId(currentUserId).setTicket(ticket)
                );
                redisService.setCacheObject(ONLINE_MEETING_PREFIX_KEY + meetingId, meetingInfo);
                // 会议超时时间
                long timeout = (meetingInfo.getEndTime().getTime() - System.currentTimeMillis()) / 1000 + 60;
                redisService.expire(ONLINE_MEETING_PREFIX_KEY + meetingId, timeout, TimeUnit.SECONDS);
                return;
            }

        } catch (Exception e) {
            log.error("dealJoin error,errors:{}", e.getMessage());
        }
    }
}
