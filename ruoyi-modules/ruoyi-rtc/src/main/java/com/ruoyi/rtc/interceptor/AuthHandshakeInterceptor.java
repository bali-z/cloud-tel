package com.ruoyi.rtc.interceptor;

import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * @author dz
 * websocket 权限拦截器
 */
@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // 获取请求头并存储到 attributes 中
        String userKey = request.getHeaders().getFirst(SecurityConstants.USER_KEY);
        String userId = request.getHeaders().getFirst(SecurityConstants.DETAILS_USER_ID);
        String userName = request.getHeaders().getFirst(SecurityConstants.DETAILS_USERNAME);
        if (StringUtils.isEmpty(userKey) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(userName)) {
            log.error("Received an illegal websocket connection!");
            return false;
        }
        // 当前连接用户存储到未来要建立的 Websocket attributes 中
        attributes.put(SecurityConstants.USER_KEY, userKey);
        attributes.put(SecurityConstants.DETAILS_USER_ID, userId);
        attributes.put(SecurityConstants.DETAILS_USERNAME, userName);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 暂时没有什么约束
        return;
    }
}
