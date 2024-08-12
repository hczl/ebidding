package com.ebidding.common.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description WebSocket 连接会话管理
 */
@Slf4j
public class WebSocketSessionManager {
    /**
     * 保存连接对象的 session 到集合中
     */
    public static ConcurrentHashMap<String,WebSocketSession> sessionGroup = new ConcurrentHashMap<>();

    /**
     * 添加 session 到集合中
     *
     * @param session 连接对象的session
     */
    public static void add(WebSocketSession session) {
        // 添加 session
        sessionGroup.put(session.getId(),session);
    }

    /**
     * 从集合中删除 session,会返回删除的 session
     *
     * @param session
     * @return
     */
    public static boolean remove(WebSocketSession session) {
        // 删除 session
        return sessionGroup.remove(session.getId(),session);
    }

    /**
     * 删除并关闭 连接
     *
     * @param session
     */
    public static void removeAndClose(WebSocketSession session) {
        if (!remove(session)) {
            try {
                // 关闭连接
                session.close();
            } catch (IOException e) {
                log.error("关闭出现异常处理",e);
            }
        }
    }


}

