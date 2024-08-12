package com.ebidding.common.utils;

import com.ebidding.common.websocket.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class WebSocketMessageUtil {



    /**
     * 发送消息给指定客户端
     * @param session 客户端session
     * @param text 发送消息的内容
     * @return
     * @throws IOException
     */
    public static synchronized void sendMsgToOne(WebSocketSession session, String text) {
        if(session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(text));
            }catch (Exception e){
                log.error("【WebSocket】发送消息失败，cause:{}",e.getMessage());
            }
        }else{
            log.error("【WebSocket】发送消息失败，cause:session is null or closed！");
        }

    }

    /**
     * 发送消息给所有客户端，客户端的session必须在WebSocketSessionManager的sessionGroup中
     * @param text 发送消息的内容
     * @return
     * @throws IOException
     */
    public static synchronized void sendMsgToAll(String text) throws IOException {
        for (Map.Entry<String, WebSocketSession> entry : WebSocketSessionManager.sessionGroup.entrySet()) {
            WebSocketSession session = entry.getValue();
            if(session.isOpen()){
                session.sendMessage(new TextMessage(text));
            }
        }
    }

}
