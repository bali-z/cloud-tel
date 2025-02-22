package com.ruoyi.rtc.handler;

import com.ruoyi.common.redis.generator.SnowflakeIdGenerator;
import com.ruoyi.common.redis.service.RedisService;
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

/**
 *  websocket 信令处理
 */
@SuppressWarnings("ALL")
@Component
public class SignalDealWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(SignalDealWebSocketHandler.class);
    /**
     *  雪花算法ID迭代器
     */
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

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
    public static final String CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION = "CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION";


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
        // 在sessiont添加生成的sessionId
        session.getAttributes().put(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION, sessionId);
        socketConnectionPool.put(sessionId, session);
        log.debug("连接建立成功 sessionId:{}", sessionId);

        // todo: 给当前连接发送一个 CONNECT_SUCCESS 信令，并将sessionId传递
    }

    /**
     * OnMessage
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("Message from:{},message:{}", session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION), payload);
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
     *  获取资源锁
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

}
