package com.ebidding.bid.websocket;

import com.ebidding.common.websocket.enums.WebSocketMsgType;
import com.ebidding.common.websocket.model.HandleTextMessageBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Description 处理接收客户端发送的文本消息
 */
@Service
@Slf4j
public class HandleTextMessageService {

    @Resource
    private MessageTypeService messageTypeService;

    private Map<String, Consumer<HandleTextMessageBO>> msgTypeMap = new HashMap<>();

    /**
     * 初始化业务分派逻辑,代替了if-else部分
     * key: 接收消息类型
     * value: 消费型函数(接收一个参数，没有返回值),最终会执行处理该消息类型
     */
    @PostConstruct
    public void dispatcherInit() {
        msgTypeMap.put(WebSocketMsgType.HEART_MSG.getCode(), handleTextMessageBO -> messageTypeService.handleHeartPing(handleTextMessageBO));
        msgTypeMap.put(WebSocketMsgType.BIND_USER_ID_MSG.getCode(), handleTextMessageBO -> messageTypeService.handleBindUserId(handleTextMessageBO));
    }

    public void handleTextMsg(String msgType,HandleTextMessageBO handleTextMessageBO) {
        Consumer<HandleTextMessageBO> result = msgTypeMap.get(msgType);
        if (result != null) {
            result.accept(handleTextMessageBO);
            return;
        }
        log.error("【WebSocket】处理接收客户端发送的文本消息，消息类型不存在，无法处理！");
    }

}
