package com.ruoyi.rtc.websocket;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.generator.SnowflakeIdGenerator;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.rtc.domain.SysMeeting;
import com.ruoyi.rtc.pojo.*;
import com.ruoyi.rtc.service.ISysMeetingService;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 只做信息调度交换，不完成实际的点对点视频通信
 *
 * @author dz
 */
@SuppressWarnings("ALL")
@ServerEndpoint("/websocket/{userToken}")
@Component
public class RtcWebSocket {

    private static final Logger log = LoggerFactory.getLogger(RtcWebSocket.class);

    /**
     * 雪花算法生成器 用于生成 sessionId
     */
    private SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * 当前webSocket连接的userToken
     */
    private String userToken;

    /**
     * sessionId key
     */
    private String sessionId;

    /**
     * sessionId value
     */
    private Session session;

    /**
     *  当前 socket连接的userId
     */
    private Long currentUserId;

    /**
     *  会议发起者 userId
     */
    private Long meetingOwnerUserId;

    /**
     *   会议发起者 sessionId
     */
    private String meetingOwnerSessionId;

    @Autowired
    private ISysMeetingService sysMeetingService;

    @Autowired
    private RedisService redisService;


    /**
     * 缓存所有连接的session
     */
    private static final ConcurrentHashMap<String, RtcWebSocket> socketConnectionPool = new ConcurrentHashMap<>();


    /**
     * 向指定session发送消息
     *
     * @param sessionId
     * @param message
     */
    public static void sendMessage(String tarketSessionId, String message) {
        try {
            RtcWebSocket tarketSocket = socketConnectionPool.get(tarketSessionId);
            Session tarketSession = tarketSocket.getSession();
            if (tarketSocket == null || tarketSession == null || !tarketSession.isOpen()) {
                return;
            }
            synchronized (tarketSession) {
                log.info("WebSocket send message to userId:{},sessionId:{},message:{}", tarketSocket.getUserToken(), tarketSessionId, message);
                tarketSession.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("WebSocket send message failed, errors:{}", e.getMessage());
        }
    }


    /**
     * @param session
     * @param userId  用户 token
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userToken") String userId) {
        try {
            String sessionId = snowflakeIdGenerator.nextId();
            socketConnectionPool.put(sessionId, new RtcWebSocket(userId, sessionId, session));
            // 发送连接成功信令，并将对应 sessionId 发送给连接端
            sendMessage(sessionId, getMessageResponse(SignalType.CONNECT_SUCCESS, sessionId));
            log.info("WebSocket connection successful userId:{},sessionId:{}", userId, sessionId);
        } catch (Exception exception) {
            log.error("WebSocket connection failed userId:{},errors:{}", userId, exception.getMessage());
        }
    }


    /**
     * 当接受到信令
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        log.debug("WebSocket receive message:{}", message);
        if (StringUtils.isEmpty(message)) {
            return;
        }
        /**
         *  获取信令信息
         */
        JSONObject messageObj = JSONObject.parseObject(message);
        // 信令
        SignalType signal;
        try {
            signal = Enum.valueOf(SignalType.class, messageObj.getString("signal"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        JSONObject data = messageObj.getJSONObject("data");
        switch (signal) {
            case PING:
                break;
            case PONG:
                break;
            case OFFER:
                break;
            case ANSWER:
                break;
            case CANDIDATE:
                break;
            case JOIN:
                break;
            case JOIN_CONFIRM:
                break;
            case JOIN_RESOLVE:
                break;
            case JOIN_REJECT:
                break;
            case ENTER:
                break;
            case HANGUP:
                break;
            case HANGUP_SUCCESS:
                break;
            default:
                break;
        }
    }

    /**
     * 发过来信令是 Ping
     */
    private void onPing() {
        sendMessage(sessionId, getMessageResponse(SignalType.PONG, "PONG"));
    }

    /**
     * 发过来信令是 Offer
     * 是要向目标 seesionId 发送自己的多媒体通信参数信息给目标连接
     *
     * @param data
     */
    private void onOffer(JSONObject data) {
        // 接受 offer
        OfferMessage offerMessage = JSONObject.toJavaObject(data, OfferMessage.class);
        String recvSessionId = offerMessage.getRecvSessionId();
        // 将 offer 信息转发给目标连接
        if (StringUtils.isEmpty(recvSessionId)) {
            return;
        }
        if (socketConnectionPool.containsKey(recvSessionId)) {
            RtcWebSocket recvSocket = socketConnectionPool.get(recvSessionId);
            if (StringUtils.isEmpty(offerMessage.getSendSessionId())) {
                offerMessage.setSendSessionId(sessionId);
            }
            // 将 offer 信息转发给目标连接
            sendMessage(recvSessionId, getMessageResponse(SignalType.OFFER, JSONObject.toJSONString(offerMessage)));
        }
    }

    /**
     * 回复信令 Answer
     * 将当前连接sessoin的 answer 信息转发给目标连接
     *
     * @param data
     */
    public void onAnswer(JSONObject data) {
        // 接受 answer
        AnswerMessage answerMessage = JSONObject.toJavaObject(data, AnswerMessage.class);
        String recvSessionId = answerMessage.getRecvSessionId();
        // 将 answer 信息转发给目标连接
        if (StringUtils.isEmpty(recvSessionId)) {
            return;
        }
        if (socketConnectionPool.containsKey(recvSessionId)) {
            RtcWebSocket recvSocket = socketConnectionPool.get(recvSessionId);
            if (StringUtils.isEmpty(answerMessage.getSendSessionId())) {
                answerMessage.setSendSessionId(sessionId);
            }
            // 将 offer 信息转发给目标连接
            sendMessage(recvSessionId, getMessageResponse(SignalType.OFFER, JSONObject.toJSONString(answerMessage)));
        }
    }

    /**
     * candidate 信令
     * 将当前连接sessoin的 candidate(NAT网络信息) 信息转发给目标连接
     *
     * @param data
     */
    public void onCandidate(JSONObject data) {
        // 接受 candidate
        CandidateMessage candidateMessage = JSONObject.toJavaObject(data, CandidateMessage.class);
        String recvSessionId = candidateMessage.getRecvSessionId();
        // 将 candidate 信息转发给目标连接
        if (StringUtils.isEmpty(recvSessionId)) {
            return;
        }
        if (socketConnectionPool.containsKey(recvSessionId)) {
            RtcWebSocket recvSocket = socketConnectionPool.get(recvSessionId);
            if (StringUtils.isEmpty(candidateMessage.getSendSessionId())) {
                candidateMessage.setSendSessionId(sessionId);
            }
            sendMessage(recvSessionId, getMessageResponse(SignalType.CANDIDATE, JSONObject.toJSONString(candidateMessage)));
        }
    }


    /**
     *  加入会议申请 信令
     *  // 在 Join阶段可以获取到 当前用户 userId 以及当前 用户的 session
     *
     * @param data
     */
    public void onJoin(JSONObject data) {
        JoinMessage joinMessage = JSONObject.toJavaObject(data, JoinMessage.class);
        // 所申请进入的 meetingId
        String meetingId = joinMessage.getMeetingId();
        String applyUserToken = joinMessage.getApplyUserToken();
        String ticket = joinMessage.getTicket();
        if (StringUtils.isEmpty(meetingId)) {
            return;
        }
        // 根据 meetingId 查询会议信息
        SysMeeting meeting = sysMeetingService.getMeetingByMeetingId(meetingId);
        Long adminUserId = meeting.getUserId();

        // 会议为空 无法加入
        if(meeting == null){
            sendMessage(sessionId, getMessageResponse(SignalType.JOIN_REJECT, "会议不存在,请核对会议ID!"));
            return;
        }
        if(StringUtils.isEmpty(applyUserToken)){
            sendMessage(sessionId, getMessageResponse(SignalType.JOIN_REJECT, "未发送Token!"));
            return;
        }

        // 会议不为空
        // 会议未开始
        long currentTimeMillis = System.currentTimeMillis();
        if(meeting.getStartTime().getTime() > currentTimeMillis){
            sendMessage(sessionId, getMessageResponse(SignalType.JOIN_REJECT, "会议尚未开始,请稍后再试!"));
            return;
        }
        // 会议已结束
        if(meeting.getEndTime().getTime() < currentTimeMillis){
            sendMessage(sessionId, getMessageResponse(SignalType.JOIN_REJECT, "会议已结束,请重新选择会议!"));
            return;
        }
        // 验证用户token是否正确
        Long userId = Long.parseLong(JwtUtils.getUserId(applyUserToken));

        // 当前用户是会议发起者
        if(userId.equals(meeting.getUserId())){
            // 发送同意加入会议信令
            // 会议管理员票据未下发补发
            BaseMessage message = new BaseMessage();
            message.setMeetingId(meetingId);
            message.setSignal(SignalType.JOIN_RESOLVE);
            message.setSendSessionId(sessionId);
            if(StringUtils.isEmpty(ticket)){
                Map<String,Object> claims = new HashMap<>();
                claims.put("meetingId", meeting.getMeetingId());
                claims.put("userid", meeting.getUserId());
                claims.put("currentUserId", userId);
                ticket = JwtUtils.createToken(claims);
                message.setTicket(ticket);
            }
            // 校验成功 准备进入会议
            sendMessage(sessionId, getMessageResponse(SignalType.JOIN_RESOLVE, JSONObject.toJSONString(message)));
            this.currentUserId = userId;
            this.meetingOwnerUserId = userId;
            this.meetingOwnerSessionId = sessionId;
            return ;
        }

        // 当前用户不是会议发起者
        // todo 内容代码多先不做
//        if(meeting.getAttendeesUserIdList().contains(userId)){
//        }
        // 校验一下票据
        if(ticket != null){
            if(checkTicket(ticket,meeting,userId)){
                BaseMessage message = new BaseMessage();
                message.setMeetingId(meetingId);
                message.setSignal(SignalType.JOIN_RESOLVE);
                message.setSendSessionId(sessionId);
                sendMessage(sessionId, getMessageResponse(SignalType.JOIN_RESOLVE, JSONObject.toJSONString(message)));
                return ;
            }else {
                // 密钥过期,让管理员审批
            }
        }

        // todo 其他情况





    }

    /**
     *  验证票据
     * @param ticket
     * @param meeting
     * @param userId
     * @return
     */
    private boolean checkTicket(String ticket, SysMeeting meeting, Long rqUserId){
        Claims claims = JwtUtils.parseToken(ticket);
        if(claims == null){
            return false;
        }
        String meetingId = claims.get("meetingId").toString();
        String userId = claims.get("userid").toString();
        String currentUserId = claims.get("currentUserId").toString();
        return meetingId.equals(meeting.getMeetingId()) && userId.equals(meeting.getUserId()) && currentUserId.equals(rqUserId);
    }




    /**
     * 生成一些简单的响应信息
     *
     * @param signalType
     * @param content
     * @return
     */
    public static String getMessageResponse(SignalType signalType, Object content) {
        if (null == signalType) {
            return null;
        }
        JSONObject message = new JSONObject();
        switch (signalType) {
            case CONNECT_SUCCESS, PONG, OFFER, ANSWER, CANDIDATE, JOIN_CONFIRM, JOIN_RESOLVE, JOIN_REJECT, ENTER, HANGUP, HANGUP_SUCCESS:
                message.put("signal", signalType);
                message.put("data", content);
                break;
            default:
                break;
        }
        return message.toJSONString();
    }


    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public RtcWebSocket() {
    }

    public RtcWebSocket(String userToken, String sessionId, Session session) {
        this.userToken = userToken;
        this.sessionId = sessionId;
        this.session = session;
    }

}
