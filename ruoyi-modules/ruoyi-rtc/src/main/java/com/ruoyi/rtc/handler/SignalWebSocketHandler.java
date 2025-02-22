package com.ruoyi.rtc.handler;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.redis.generator.SnowflakeIdGenerator;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.rtc.domain.SysMeeting;
import com.ruoyi.rtc.pojo.RtcR;
import com.ruoyi.rtc.pojo.SignalBase;
import com.ruoyi.rtc.pojo.SignalType;
import com.ruoyi.rtc.pojo.forward.*;
import com.ruoyi.rtc.pojo.handle.JoinSignal;
import com.ruoyi.rtc.service.ISysMeetingService;
import com.ruoyi.system.api.RemoteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.ruoyi.rtc.pojo.Constants.DATA;
import static com.ruoyi.rtc.pojo.Constants.SIGNAL;

/**
 * @author dz
 * describe: 基于 spring 的websocket实现
 */
@SuppressWarnings("ALL")
@Component
public class SignalWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(SignalWebSocketHandler.class);
    /**
     * 雪花算法生成器 用于生成 sessionId
     */
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    private ISysMeetingService sysMeetingService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RemoteUserService remoteUserService;

    /**
     * 缓存所有连接的session
     */
    private static final ConcurrentHashMap<String, WebSocketSession> socketConnectionPool = new ConcurrentHashMap<>();

    /**
     * 会议锁
     */
    public static final ConcurrentHashMap<String, Lock> meetingLockPool = new ConcurrentHashMap<>();

    /**
     * 生成的 sessionId，连接建立完成后分配
     */
    public static final String CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION = "currentSessionId";


    /**
     * 向那个 session 发送消息
     *
     * @param message
     * @param sessionId
     */
    public static void sendMessage(String message, String sessionId) {
        WebSocketSession session = socketConnectionPool.get(sessionId);
        if (null == session) {
            log.debug("sessionId:{},unexists!", sessionId);
            return;
        }
        synchronized (session) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 相当于 OnOpen
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = snowflakeIdGenerator.nextId() + ":" + session.getId();
        session.getAttributes().put(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION, sessionId);
        socketConnectionPool.put(sessionId, session);
        log.debug("连接建立成功 sessionId:{}", sessionId);

        // 给当前连接发送一个 CONNECT_SUCCESS 信令，并将sessionId传递
        sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.CONNECT_SUCCESS, "", sessionId)), sessionId);
    }

    /**
     * OnMessage
     * 所接受数据格式为 RtcR
     * {
     * code int,
     * msg String,
     * data T,
     * signal SignalType
     * }
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("Message from:{},message:{}", session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION), payload);
        try {
            JSONObject messageObj = JSONObject.parseObject(payload);
            String signalType = messageObj.getString(SIGNAL);
            SignalType type = Enum.valueOf(SignalType.class, signalType);
            JSONObject data = messageObj.getJSONObject(DATA);
            // 接收到 type
            switch (type) {
                // 接收到心跳包
                case PING -> {
                    // 响应 pong
                    sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.PONG, "", "")), (String) session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
                    break;
                }
                // 接受到JOIN信令 (校验用户是否有资格参会)
                case JOIN -> {
                    JoinSignal.dealJoin(sysMeetingService, redisService, remoteUserService, data, session);
                    break;
                }
                // 会议发起者拒绝请求加入会议的人参会
                case JOIN_REJECT -> {
                    JoinRejectInfo joinRejectInfo = JSONObject.toJavaObject(data, JoinRejectInfo.class);
                    // 给目标session发送拒绝参会即可
                    sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.JOIN_REJECT, "管理员拒绝你的入会请求!", "管理员拒绝你的入会请求!")), joinRejectInfo.getTargetSessionId());
                    break;
                }
                // 会议发起者同意加入会议请求转发
                case JOIN_RESOLVE -> {
                    JoinResolveInfo joinResolveInfo = JSONObject.toJavaObject(data, JoinResolveInfo.class);
                    // 给请求者一个ticket,这样的话就可以顺利进入会议
                    String meetingId = joinResolveInfo.getMeetingId();
                    SysMeeting meetingByMeetingId = sysMeetingService.getMeetingByMeetingId(meetingId);
                    String ticket = SignalBase.createTicket(meetingByMeetingId, joinResolveInfo.getTargetUserId());
                    sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.JOIN_RESOLVE, "agree!", ticket)), joinResolveInfo.getTargetSessionId());
                    break;
                }
                // 接受到Enter信令 (参会)
                case ENTER -> {
                    EnterInfo.dealEnter(sysMeetingService, redisService, remoteUserService, data, session);
                    break;
                }
                // offer 交换媒体流信息
                case OFFER -> {
                    OfferAnswer offerAnswer = JSONObject.toJavaObject(data, OfferAnswer.class);
                    offerAnswer.setSourceSessionId((String) session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
                    // 将媒体流信息发送给对方
                    sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.OFFER, "OFFER!", offerAnswer)), offerAnswer.getTargetSessionId());
                    break;
                }
                // offer 交换媒体流信息
                case ANSWER -> {
                    OfferAnswer offerAnswer = JSONObject.toJavaObject(data, OfferAnswer.class);
                    offerAnswer.setSourceSessionId((String) session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
                    // 将媒体流信息发送给对方
                    sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.ANSWER, "ANSWER!", offerAnswer)), offerAnswer.getTargetSessionId());
                    break;
                }
                // 交换网络信息
                case CANDIDATE -> {
                    CandidateInfo candidateInfo = JSONObject.toJavaObject(data, CandidateInfo.class);
                    candidateInfo.setSourceSessionId((String) session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
                    // 将媒体流信息发送给对方
                    sendMessage(JSONObject.toJSONString(RtcR.instance(Constants.SUCCESS, SignalType.CANDIDATE, "CANDIDATE!", candidateInfo)), candidateInfo.getTargetSessionId());
                    break;
                }
                // 挂断信令
                case HANGUP -> {
                    HangUpInfo.dealHangUp(sysMeetingService, redisService, remoteUserService, data, session);
                    break;
                }

                default -> {
                    log.info("receive message sessionId:{},message:{},unknow siginal!", session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION), payload);
                    break;
                }
            }
        } catch (Exception exception) {
            log.error("WebSocket receive message failed,message:{} errors:{}", message, exception.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.debug("session:{} offline!", session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
        socketConnectionPool.remove(session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
    }


    public static void removeMeetingLock(String meetingId) {
        meetingLockPool.remove(meetingId);
    }

    /**
     * 使用懒汉模式，获取锁
     *
     * @param meetingId
     * @return
     */
    public static Lock getMeetingLock(String meetingId) {
        Lock lock = meetingLockPool.get(meetingId);
        if (null == lock) {
            synchronized (SignalWebSocketHandler.class) {
                // 上锁成功，但有可能lock已经有了所以一定要没有才能new出来
                if (null == lock) {
                    lock = new ReentrantLock();
                    meetingLockPool.put(meetingId, lock);
                }
            }
        }
        return lock;
    }

}
