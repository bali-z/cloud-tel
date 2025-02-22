package com.ruoyi.rtc.meeting;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.pojo.MeetingProcess;
import com.ruoyi.common.core.utils.ticket.TicketUtil;
import com.ruoyi.common.redis.util.MeetingProcessUtil;
import com.ruoyi.rtc.handler.SignalDealWebSocketHandler;
import com.ruoyi.rtc.meeting.signal.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.ruoyi.rtc.business.BaseMessage.CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION;

/**
 * 处理会议信号的处理器
 *
 * @author dz
 */
@SuppressWarnings("ALL")
@Component
public class MeetingSignalHandler {

    @Autowired
    private MeetingProcessUtil meetingProcessUtil;

    private static final Logger log = LoggerFactory.getLogger(MeetingSignalHandler.class);
    /**
     * 会议锁
     */
    public static final ConcurrentHashMap<String, Lock> meetingLockPool = new ConcurrentHashMap<>();

    public static void removeMeetingLock(String meetingId) {
        meetingLockPool.remove(meetingId);
    }

    /**
     * 获取资源锁
     *
     * @param resourceId
     * @return
     */
    public static Lock getLockByResourceId(String resourceId) {
        Lock lock = meetingLockPool.get(resourceId);
        if (null == lock) {
            synchronized (SignalDealWebSocketHandler.class) {
                // 上锁成功，但有可能lock已经有了所以一定要没有才能new出来
                if (null == lock) {
                    lock = new ReentrantLock();
                    meetingLockPool.put(resourceId, lock);
                }
            }
        }
        return lock;
    }

    public void dealMessage(WebSocketSession session, String message) {
        log.debug("dealMessage:{}", message);
        try {
            MeetingSignal meetingSignal = JSONObject.parseObject(message, MeetingSignal.class);

            switch (meetingSignal.getCode()) {
                case PING -> {
                    SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(new Pong()), (String) session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
                    break;
                }
                case JOIN -> {
                    dealJoin(session, message);
                    break;
                }


                default -> {
                    log.error("undefined code error:{}", message);
                    break;
                }
            }


        } catch (Exception e) {
            log.error("dealMessage error:{}", e.getMessage());
        }
    }

    /**
     * 处理JOIN信令
     *
     * @param session
     * @param message
     */
    private void dealJoin(WebSocketSession session, String message) {
        //1.解析消息
        Join join = JSONObject.parseObject(message, Join.class);
        String meetingId = join.getMeetingId();
        String ticket = join.getTicket();
        Long userId = Long.parseLong((String) session.getAttributes().get(SecurityConstants.DETAILS_USER_ID));
        String userName = (String) session.getAttributes().get(SecurityConstants.DETAILS_USERNAME);
        String currentSessionId = (String) session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION);

        //2.获取会议信息
        MeetingProcess meetingProcess = meetingProcessUtil.getMeetingProcess(meetingId);
        if (null == meetingProcess) {
            log.error("meetingProcess is null,meetingId:{}", meetingId);
            return;
        }
        //校验当前用户是不是会议发起者
        if (meetingProcess.getHoldUserId().equals(userId)) {
            //如果是会议发起者则直接给发起者发消息
            String newTicket = null;
            // 如果票据失效则重新生成一个
            if (!TicketUtil.verifyTicket(ticket, meetingId, meetingProcess.getHoldUserId(), userId)) {
                newTicket = TicketUtil.createTicket(meetingId, meetingProcess.getHoldUserId(), userId);
            }
            JoinResolve resolve = new JoinResolve();
            resolve.setMeetingId(meetingId);
            resolve.setTicket(newTicket);
            // 发送JOIN_RESOLVE信令
            SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(resolve), currentSessionId);
            return;
        }
        //不是会议发起者,老老实实校验票据
        if (TicketUtil.verifyTicket(ticket, meetingId, meetingProcess.getHoldUserId(), userId)) {
            JoinResolve resolve = new JoinResolve();
            resolve.setMeetingId(meetingId);
            // 发送JOIN_RESOLVE信令
            SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(resolve), currentSessionId);
            return;
        }
        // 票据校验不过
        // 需要问一下管理员是否允许入会
        Set<MeetingProcess.MeetingMember> meetingMembers = meetingProcess.getMeetingMembers();
        // 如果会议管理员已经入会可以问一下，没入会就直接给拒绝了
        MeetingProcess.MeetingMember meetingMember = new MeetingProcess.MeetingMember();
        meetingMember.setUserId(meetingProcess.getHoldUserId());

        if (meetingMembers.contains(meetingMember)) {
            // 会议管理员在
            // 找到会议管理员
            for (MeetingProcess.MeetingMember member : meetingMembers) {
                if (member.getUserId().equals(meetingProcess.getHoldUserId())) {
                    // 给会议管理员发送JOIN_CONFIRM信令
                    JoinConfirm joinConfirm = new JoinConfirm();
                    joinConfirm.setMeetingId(meetingId);
                    joinConfirm.setName(userName);
                    joinConfirm.setSourceSessionId(currentSessionId);
                    joinConfirm.setSourceUserId(userId);
                    SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(joinConfirm), member.getSessionId());
                    return;
                }
            }
        }
        // 会议管理员没入会,直接给拒绝了
        JoinReject reject = new JoinReject();
        reject.setMeetingId(meetingId);
        SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(reject), currentSessionId);
    }
}
