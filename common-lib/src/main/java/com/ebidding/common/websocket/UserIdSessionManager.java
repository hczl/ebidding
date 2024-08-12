package com.ebidding.common.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description WebSocket 用户ID\socket连接
 */
@Slf4j
public class UserIdSessionManager {

    public static ConcurrentHashMap<Integer,WebSocketSession> sessionGroup = new ConcurrentHashMap<>();

    /**
     * 添加 session 到集合中
     *
     * @param session 连接对象的session
     */
    public static void add(Integer userId,WebSocketSession session) {
        // 添加 session
        sessionGroup.put(userId,session);
    }

    /**
     * 从集合中删除 session,会返回删除的 session
     *
     * @param session
     * @return
     */
    public static boolean remove(Integer userId,WebSocketSession session) {
        // 删除 session
        return sessionGroup.remove(userId,session);
    }

    /**
     * 删除并关闭 连接
     *
     * @param session
     */
    public static void removeAndClose(Integer userId,WebSocketSession session) {
        if (!remove(userId,session)) {
            try {
                // 关闭连接
                session.close();
            } catch (IOException e) {
                log.error("关闭出现异常处理",e);
            }
        }
    }

    public static WebSocketSession getSession(Integer userId) {
        return sessionGroup.get(userId);
    }
    public static ConcurrentHashMap<Integer,WebSocketSession> getAllSession() {
        return sessionGroup;
    }


}

