package com.ruoyi.rtc.meeting;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.pojo.MeetingProcess;
import com.ruoyi.common.core.utils.ticket.TicketUtil;
import com.ruoyi.common.redis.util.MeetingProcessUtil;
import com.ruoyi.rtc.handler.SignalDealWebSocketHandler;
import com.ruoyi.rtc.meeting.signal.*;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysUser;
import org.bouncycastle.jcajce.provider.symmetric.AES;
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
    private RemoteUserService remoteUserService;

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
                case JOIN_REJECT -> {
                    dealJoinReject(session, message);
                    break;
                }
                case JOIN_RESOLVE -> {
                    dealJoinResolve(session, message);
                    break;
                }
                case ENTER -> {
                    dealEnter(session, message);
                    break;
                }
                case OFFER -> {
                    dealOffer(session, message);
                    break;
                }
                case ANSWER -> {
                    dealAnswer(session, message);
                    break;
                }
                case CANDIDATE -> {
                    dealCandidate(session, message);
                    break;
                }
                case HANG_UP -> {
                    dealHangUp(session, message);
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
     *  挂断
     *  前端发送
     *  {
     *      "businessType": "MEETING",
     *      "code": "HANG_UP",
     *      "meetingId": "12212121121",
     *      "sourceUserId": 1
     *      "name": "ry",
     *      "avatar": "https://gitee.com/dromara/ruoyi-vue-pro/raw/dev/src/assets/images/avatar.jpg"
     *  }
     * 后端转发：
     *      相同数据
     * @param session
     * @param message
     */
    private void dealHangUp(WebSocketSession session, String message) {

        HangUp hangUp = JSONObject.parseObject(message, HangUp.class);
        String meetingId = hangUp.getMeetingId();
        Long sourceUserId = hangUp.getSourceUserId();

        MeetingProcess meetingProcess = meetingProcessUtil.getMeetingProcess(meetingId);
        meetingProcess.getMeetingMembers().removeIf(member -> member.getUserId().equals(sourceUserId));

        // 转发给其他人
        meetingProcess.getMeetingMembers().forEach(member -> {
            SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(hangUp), member.getSessionId());
        });
    }

    private void dealCandidate(WebSocketSession session, String message) {
        Candidate candidate = JSONObject.parseObject(message, Candidate.class);
        candidate.setSourceSessionId((String) session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
        String targetSessionId = candidate.getTargetSessionId();
        SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(candidate), targetSessionId);
    }

    private void dealAnswer(WebSocketSession session, String message) {
        Answer answer = JSONObject.parseObject(message, Answer.class);
        answer.setSourceSessionId((String) session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
        String targetSessionId = answer.getTargetSessionId();
        SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(answer), targetSessionId);
    }


    /**
     *  前端发送
     *      {
     *          "businessType": "MEETING",
     *          "code": "OFFER",
     *          description:{},
     *          "sourceSessionId": "xxxxxxxxxxxx",
     *          "targetSessionId": "xxxxxxxxxxxx",
     *
     *      },
     * 后端转发：
     *      {
     *          "businessType": "MEETING",
     *          "code": "ANSWER",
     *          "description":{},
     *          "sourceSessionId": "xxxxxxxxxxxx",
     *          "targetSessionId": "xxxxxxxxxxxx",
     *
     *      }
     *
     * @param session
     * @param message
     */
    private void dealOffer(WebSocketSession session, String message) {
        Offer offer = JSONObject.parseObject(message, Offer.class);
        offer.setSourceSessionId((String) session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
        String targetSessionId = offer.getTargetSessionId();
        SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(offer), targetSessionId);
    }


    /**
     * 前端发送
     *  {
     *      "businessType": "MEETING",
     *      "code": "ENTER",
     *      "meetingId": "1234454",
     *      "ticket": "xxsdada.xcasdad"
     *  }
     *
     * 返回：
     *   给其他人发送
     *      {
     *       "businessType": "MEETING",
     *        "code": "ENTER",
     *         "sourceUserId": 2,
     *         "sourceSessionId": "xxxxxxxxxxxx",
     *          "name": "xxx",
     *          "avatar": ""
     *      }
     *
     *
     *
     * @param session
     * @param message
     */
    private void dealEnter(WebSocketSession session, String message) {
        Enter enter = JSONObject.parseObject(message, Enter.class);
        String meetingId = enter.getMeetingId();
        String ticket = enter.getTicket();
        Long userId = Long.parseLong((String) session.getAttributes().get(SecurityConstants.DETAILS_USER_ID));
        String userName = (String) session.getAttributes().get(SecurityConstants.DETAILS_USERNAME);
        String currentSessionId = (String) session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION);

        //1.获取会议
        MeetingProcess meetingProcess = meetingProcessUtil.getMeetingProcess(meetingId);
        if (null == meetingProcess) {
            log.error("meetingProcess is null,meetingId:{}", meetingId);
            return;
        }
        //2.校验ticket
        if (!TicketUtil.verifyTicket(ticket,meetingId,meetingProcess.getHoldUserId(),userId)) {
            log.error("ticket is error,meetingId:{},ticket:{}", meetingId, ticket);
            return;
        }
        //3.票据校验通
        // 构建会议人员对象添加到会议中
        // 从用户服务中获取用户信息

        R<SysUser> userInfoById = remoteUserService.getUserInfoById(userId, SecurityConstants.INNER);
        MeetingProcess.MeetingMember meetingMember = new MeetingProcess.MeetingMember();
        meetingMember.setUserId(userId);
        meetingMember.setName(userInfoById.getData().getUserName());
        meetingMember.setAvatarUrl(userInfoById.getData().getAvatar());
        meetingMember.setSessionId(currentSessionId);

        Enter enterInfo = new Enter();
        enterInfo.setMeetingId(meetingId);
        enterInfo.setSourceUserId(userId);
        enterInfo.setSourceSessionId(currentSessionId);
        enterInfo.setName(userInfoById.getData().getUserName());
        enterInfo.setAvatar(userInfoById.getData().getAvatar());

        // 判断一下是不是管理员发送的ENTER信令
        if (meetingProcess.getHoldUserId().equals(userId)) {
            meetingProcess.setHoldUserSessionId(currentSessionId);
        }
        // 将用户加入会议中
        meetingProcess.getMeetingMembers().add(meetingMember);
        meetingProcessUtil.cacheMeetingProcess(meetingProcess);
        // 给其他成员发送enter信息
        meetingProcess.getMeetingMembers().forEach(member -> {
            if(!member.equals(meetingMember)){
                SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(enterInfo),member.getSessionId());
            }
        });
    }

    /**
     *
     * 前端发送
     *  {
     *     "businessType": "MEETING",
     *     "code": "JOIN_RESOLVE",
     *     "meetingId": "284231526927962112",
     *     "targetSessionId": ”bbbb:xxxxxxx“,
     *     "targetUserId": 1
     * },
     *后端转发：
     *  {
     *     "businessType": "MEETING",
     *     "code": "JOIN_RESOLVE",
     *     "meetingId": "284231526927962112",
     *     "ticket": "xxxxx.xxadaddadc",
     *     "userId": 2,
     *     "name": "ry",
     *     "avatar": ""
     * }
     *
     *  处理加入会议信令
     * @param session
     * @param message
     */
    private void dealJoinResolve(WebSocketSession session, String message) {
        // 管理员同意入会，给这个比一个ticket
        JoinResolve joinResolve = JSONObject.parseObject(message, JoinResolve.class);
        String meetingId = joinResolve.getMeetingId();
        Long targetUserId = joinResolve.getTargetUserId();

        R<SysUser> userInfoById = remoteUserService.getUserInfoById(targetUserId, SecurityConstants.INNER);

        // 获取会议信息
        MeetingProcess meetingProcess = meetingProcessUtil.getMeetingProcess(meetingId);
        if (null == meetingProcess) {
            log.error("meetingProcess is null,meetingId:{}", meetingId);
            return;
        }
        // 生成ticket
        String ticket = TicketUtil.createTicket(meetingId, meetingProcess.getHoldUserId(),targetUserId);

        JoinResolve resolve = new JoinResolve();
        resolve.setMeetingId(meetingId);
        resolve.setTicket(ticket);
        resolve.setUserId(targetUserId);
        resolve.setName(userInfoById.getData().getUserName());
        resolve.setAvatar(userInfoById.getData().getAvatar());
        resolve.setTargetSessionId(joinResolve.getTargetSessionId());
        resolve.setTargetUserId(joinResolve.getTargetUserId());

        // 把joinReject消息转发即可
        SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(resolve), resolve.getTargetSessionId());
    }

    /**
     * 处理JOIN_REJECT信令
     * @param session
     * @param message
     */
    private void dealJoinReject(WebSocketSession session, String message) {
        // 把消息转发即可
        JoinReject joinReject = JSONObject.parseObject(message, JoinReject.class);
        String targetSessionId = joinReject.getTargetSessionId();
        // 把joinReject消息转发即可
        SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(joinReject), targetSessionId);
    }


    /**
     * 处理JOIN信令
     *
     * 前端发送：
     * {
     *     "meetingId": "284231526927962112",
     *     "ticket": "xxxxx.xxadaddadc",
     *     "code": "JOIN",
     *     "businessType": "MEETING"
     * }
     *
     * 后端返回:
     * 鉴权成功：
     *  {
     *     "businessType": "MEETING",
     *     "code": "JOIN_RESOLVE",
     *     "meetingId": "284231526927962112",
     *     "ticket": "xxxxx.xxadaddadc",
     *     "userId": 2,
     *     "name": "ry",
     *     "avatar": "",
     * }
     * 鉴权失败：
     *     发送JOIN_CONFIRM信令给会议管理源
     *           {
     *               "businessType": "MEETING",
     *               "code": "JOIN_CONFIRM",
     *               "meetingId": "284231526927962112",
     *               "name": "ry",
     *               "avatar": "",
     *               "sourceUserId": 2,
     *               "userId": 2
     *           }
     *
     *     发送JOIN_REJECT
     *     {
     *         "businessType": "MEETING",
     *         "code": "JOIN_REJECT",
     *         "meetingId": "284231526927962112"
     *     }
     *
     *
     *
     *
     *
     *
     *
     *
     *
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
        R<SysUser> userInfoById = remoteUserService.getUserInfoById(userId, SecurityConstants.INNER);
        //校验当前用户是不是会议发起者
        if (meetingProcess.getHoldUserId().equals(userId)) {
            //如果是会议发起者则直接给发起者发消息
            String newTicket = null;
            // 如果票据失效则重新生成一个
            if (!TicketUtil.verifyTicket(ticket, meetingId, meetingProcess.getHoldUserId(), userId)) {
                newTicket = TicketUtil.createTicket(meetingId, meetingProcess.getHoldUserId(), userId);
            }
            // 允许加入会议并把用户信息发送给发起者
            JoinResolve resolve = new JoinResolve();
            resolve.setMeetingId(meetingId);
            resolve.setTicket(newTicket);
            resolve.setUserId(userId);
            resolve.setName(userInfoById.getData().getUserName());
            resolve.setAvatar(userInfoById.getData().getAvatar());
            // 发送JOIN_RESOLVE信令
            SignalDealWebSocketHandler.sendMessage(JSONObject.toJSONString(resolve), currentSessionId);
            return;
        }
        //不是会议发起者,老老实实校验票据
        if (TicketUtil.verifyTicket(ticket, meetingId, meetingProcess.getHoldUserId(), userId)) {
            JoinResolve resolve = new JoinResolve();
            resolve.setMeetingId(meetingId);
            resolve.setTicket(ticket);
            resolve.setUserId(userId);
            resolve.setName(userInfoById.getData().getUserName());
            resolve.setAvatar(userInfoById.getData().getAvatar());
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
                    joinConfirm.setName(userInfoById.getData().getUserName());
                    joinConfirm.setAvatar(userInfoById.getData().getAvatar());
                    joinConfirm.setSourceSessionId(currentSessionId);
                    joinConfirm.setSourceUserId(userId);
                    joinConfirm.setUserId(userId);
                    joinConfirm.setTargetSessionId(member.getSessionId());

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

    public void removeAllMemberFromMeetingProcess(Long userId) {
        meetingProcessUtil.removeAllMemberFromMeetingProcess(userId);
    }
}
