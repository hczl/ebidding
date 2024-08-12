package com.ebidding.common.websocket.model;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.Serializable;

/**
 * @Description 处理接收客户端文本消息BO
 */
@Data
@Builder
public class HandleTextMessageBO implements Serializable {

    private static final long serialVersionUID = -872245355580523788L;

    private WebSocketSession session;

    private TextMessage message;
}