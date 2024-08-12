package com.ebidding.bid.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ebidding.common.utils.WebSocketMessageUtil;
import com.ebidding.common.websocket.WebSocketSessionManager;
import com.ebidding.common.websocket.enums.WebSocketMsgType;
import com.ebidding.common.websocket.model.HandleTextMessageBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.annotation.Resource;

/**
 * @Description WebSocket 回调
 */
@Slf4j
@Component
public class WebSocketServerHandler extends AbstractWebSocketHandler {

    @Resource
    private HandleTextMessageService handleTextMessageService;

    public WebSocketServerHandler(){
        System.out.println("初始化webSocket");
    }

    /**
     * 连接成功后触发
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("【WebSocket】客户端连接成功,sessionId ==> {}", session.getId());
        // 客户端建立连接,将客户端的session对象加入到WebSocketSessionManager的sessionGroup中
        WebSocketSessionManager.add(session);
        // 将连接结果返回给客户端
        // webSocketMessageUtil.sendMsgToOne(session,session.getId() + " 连接成功" + LocalDateTime.now());
    }

    /**
     * 关闭socket连接后触发
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("【WebSocket】客户端关闭连接成功,sessionId ==> {}", session.getId());
        // 关闭连接,从WebSocketSessionManager的sessionGroup中移除连接对象
        WebSocketSessionManager.removeAndClose(session);
    }

    /**
     * 接收客户端发送的文本消息
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("【WebSocket】接收客户端发送的文本消息,sessionId ==> {},msg ==> {}", session.getId(), message.getPayload());
        JSONObject jsonObject = JSONUtil.parseObj(message.getPayload());
        String msgTypeCode = jsonObject.getStr("msgType");
        if(msgTypeCode == null || msgTypeCode.equals("") || WebSocketMsgType.getMsgType(msgTypeCode) == null){
            log.info("【WebSocket】接收客户端发送的文本消息，消息类型不存在！");
            WebSocketMessageUtil.sendMsgToOne(session,"消息类型不存在！");
            return;
        }
        HandleTextMessageBO handleTextMessageBO = HandleTextMessageBO.builder().session(session).message(message).build();
        handleTextMessageService.handleTextMsg(msgTypeCode,handleTextMessageBO);
    }

    /**
     * 接收客户端发送的二进制消息
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        log.info("【WebSocket】接收客户端发送的二进制消息,sessionId ==> {},msg ==> {}", session.getId(), message.getPayload());
    }

    /**
     * 异常处理
     *
     * @param session
     * @param exception
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("【WebSocket】异常：" + exception.getMessage(), exception);
        // 出现异常则关闭连接,从WebSocketSessionManager的sessionGroup中移除连接对象
        WebSocketSessionManager.removeAndClose(session);
    }
}

