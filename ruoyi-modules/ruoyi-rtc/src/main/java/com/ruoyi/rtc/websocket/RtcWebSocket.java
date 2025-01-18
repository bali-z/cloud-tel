package com.ruoyi.rtc.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  只做信息调度交换，不完成实际的点对点视频通信
 * @author dz
 */
@SuppressWarnings("ALL")
@ServerEndpoint("/websocket/{userId}")
@Component
public class RtcWebSocket {

    private static final Logger log = LoggerFactory.getLogger(RtcWebSocket.class);
    /**
     * sessionList key
     */
    private String sessionId;

    /**
     *  存储每个连接的session
     */
    private List<Session> sessionList;

    /**
     *  缓存所有连接的session
     */
    private static final ConcurrentHashMap<String, List<Session>> sessionPool = new ConcurrentHashMap<>();

    /**
     *  连接建立成功调用的方法
     *      连接建立后，生成一个sessionId，
     *      并把sessionId作为key,sessionList作为value,放入sessionPool中
     *
     * @param session
     * @param userId 用户 token
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        try {
            this.sessionId = session.getId() + UUID.randomUUID().toString();
            this.sessionList = sessionPool.get(this.sessionId);
            if (this.sessionList == null) {
                this.sessionList = new ArrayList<>();
            }
            this.sessionList.add(session);
            sessionPool.put(this.sessionId, this.sessionList);
            // TODO: Send the successful connection establishment command to the connecting party
//            session.getAsyncRemote().sendText(this.sessionId);





            log.info("WebSocket connection successful userId:{},sessionId:{}", userId,sessionId);
        }catch (Exception exception){
            log.error("WebSocket connection failed userId:{},errors:{}", userId,exception.getMessage());
        }
    }





}
