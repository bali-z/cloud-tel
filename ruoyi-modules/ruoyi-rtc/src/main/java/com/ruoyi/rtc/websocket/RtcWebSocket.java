package com.ruoyi.rtc.websocket;

import com.ruoyi.common.redis.generator.SnowflakeIdGenerator;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.rtc.service.ISysMeetingService;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 只做信息调度交换，不完成实际的点对点视频通信
 *
 * @author dz
 */
@SuppressWarnings("ALL")
@ServerEndpoint("/websocket/{userToken}")
@Component
@Data
@Accessors(chain = true)
public class RtcWebSocket {

    private static final Logger log = LoggerFactory.getLogger(RtcWebSocket.class);

    /**
     * 雪花算法生成器 用于生成 sessionId
     */
    private SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * 连接建立时 就有这个数据了
     * 当前连接发过来的系统用户的登录 token
     */
    private String currentUserToken;

    /**
     * 连接建立时有
     * 当前连接 sessionId
     */
    private String currentUserSessionId;

    /**
     * 连接建立时有
     * 当前连接的 session
     */
    private Session currentUserSession;

    /**
     * 建立时有
     * 当前连接的userId
     */
    private Long currentUserId;

    /**
     * 当前用户发起加入会议信令时有
     * <p>
     * 会议发起者 userId
     */
    private Long meetingOwnerUserId;

    /**
     * 当前用户发起加入会议信令时有
     * 会议发起者 sessionId
     */
    private String meetingOwnerSessionId;
    /**
     * 当前用户发起加入会议信令时有
     * 会议id
     */
    private String currentMeetingId;


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
            Session tarketSession = tarketSocket.currentUserSession;
            if (tarketSocket == null || tarketSession == null || !tarketSession.isOpen()) {
                return;
            }
            synchronized (tarketSession) {
                log.info("WebSocket send message to userToken:{},sessionId:{},message:{}", tarketSocket.currentUserToken, tarketSessionId, message);
                tarketSession.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("WebSocket send message failed, errors:{}", e.getMessage());
        }
    }

}
