package com.ruoyi.rtc.handler;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.redis.generator.SnowflakeIdGenerator;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.rtc.pojo.ResponseR;
import com.ruoyi.rtc.pojo.SignalType;
import com.ruoyi.rtc.service.ISysMeetingService;
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


    /**
     * 缓存所有连接的session
     */
    private static final ConcurrentHashMap<String, WebSocketSession> socketConnectionPool = new ConcurrentHashMap<>();

    /**
     *  生成的 sessionId，连接建立完成后分配
     */
    public String CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION = "currentSessionId";


    /**
     *  向那个 session 发送消息
     * @param message
     * @param sessionId
     */
    public static void sendMessage(String message,String sessionId) {
        WebSocketSession session = socketConnectionPool.get(sessionId);
        if(null == session){
            log.debug("sessionId:{},unexists!",sessionId);
            return;
        }
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *  相当于 OnOpen
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = snowflakeIdGenerator.nextId() + ":" + session.getId();
        session.getAttributes().put(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION, sessionId);
        socketConnectionPool.put(sessionId, session);
        log.debug("连接建立成功 sessionId:{}",sessionId);

        // 给当前连接发送一个 CONNECT_SUCCESS 信令，并将sessionId传递
        ResponseR responseR = new ResponseR().setSignal(SignalType.CONNECT_SUCCESS).setCode(200).setData(sessionId);
        sendMessage(JSONObject.toJSONString(responseR),sessionId);
    }

    /**
     *  OnMessage
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("Message from:{},message:{}",session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION), payload);
        try {
            JSONObject messageObj = JSONObject.parseObject(payload);
            String signalType = messageObj.getString(SIGNAL);
            SignalType type = Enum.valueOf(SignalType.class, signalType);
            JSONObject data = messageObj.getJSONObject(DATA);
            // 接收到 type
            switch (type) {
                case PING -> {
                    // 响应 pong
                    ResponseR responseR = new ResponseR().setSignal(SignalType.PONG).setCode(200).setData("");
                    sendMessage(JSONObject.toJSONString(responseR),(String)session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
                    break;
                }
//                case JOIN -> {
//                    JoinSignal.dealJoin(redisService,this,data);
//                    break;
//                }
                default -> { log.info("receive message sessionId:{},message:{},unknow siginal!",session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION),payload); break;}
            }
        }catch (Exception exception){
            log.error("WebSocket receive message failed,message:{} errors:{}", message,exception.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.debug("session:{} offline!",session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
        socketConnectionPool.remove(session.getAttributes().get(CURRENT_SESSION_ID_IN_WEBSOCKET_SESSION));
    }

}
