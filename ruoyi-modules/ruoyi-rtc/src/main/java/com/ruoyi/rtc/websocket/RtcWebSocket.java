package com.ruoyi.rtc.websocket;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.generator.SnowflakeIdGenerator;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.rtc.pojo.ResponseR;
import com.ruoyi.rtc.pojo.SignalType;
import com.ruoyi.rtc.pojo.handle.JoinSignal;
import com.ruoyi.rtc.service.ISysMeetingService;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

import static com.ruoyi.rtc.pojo.Constants.DATA;
import static com.ruoyi.rtc.pojo.Constants.SIGNAL;

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

    /**
     *  建立连接后发送建立成功信息，并将所属 sessionId 告知客户端
     * @param session
     * @param currentUserToken
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userToken") String currentUserToken) {
        String userId = JwtUtils.getUserId(currentUserToken);
        // 生成sessionId,并给当前连接初始化currentUserId,currentUserToken,currentUserSession,currentUserSessionId
        this.currentUserSessionId = "session:" + snowflakeIdGenerator.nextId();
        this.currentUserId = Long.valueOf(userId);
        this.currentUserToken = currentUserToken;
        this.currentUserSession = session;

        socketConnectionPool.put(this.currentUserSessionId,this);
        // 发送一个连接成功信令
        ResponseR responseR = new ResponseR().setSignal(SignalType.CONNECT_SUCCESS).setCode(200).setData(this.currentUserSessionId);
        sendMessage(this.currentUserSessionId, JSONObject.toJSONString(responseR));
    }


    @OnMessage
    public void onMessage(String message){
        if(StringUtils.isEmpty(message)){
            return;
        }
        try {
            log.debug("WebSocket receive message:{}",message);
            JSONObject messageObj = JSONObject.parseObject(message);
            String signalType = messageObj.getString(SIGNAL);
            SignalType type = Enum.valueOf(SignalType.class, signalType);
            JSONObject data = messageObj.getJSONObject(DATA);
            // 接收到 type
            switch (type) {
                case PING -> {
                    // 响应 pong
                    ResponseR responseR = new ResponseR().setSignal(SignalType.PONG).setCode(200);
                    sendMessage(this.currentUserSessionId, JSONObject.toJSONString(responseR));
                    break;
                }
                case JOIN -> {
                    JoinSignal.dealJoin(redisService,this,data);
                    break;
                }
            }
        }catch (Exception exception){
            log.error("WebSocket receive message failed,message:{} errors:{}", message,exception.getMessage());
        }
    }





}
