package com.ebidding.common.websocket.enums;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author
 * @Description WebSocket 消息类型
 */
@Getter
public enum WebSocketMsgType {

    /** 接收消息类型 **/
    HEART_MSG("HeartPing", "心跳消息"),
    BIND_USER_ID_MSG("BindUserId", "socket连接绑定用户ID"),

    /** 发送消息类型 **/
    NOTICE_RANK_CHANGE_MSG("NoticeRankChange", "通知用户排名发生变化"),
    NOTICE_RESULT_MSG("NoticeResult", "通知用户竞拍结果");

    private final String code;

    private final String message;

    WebSocketMsgType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static WebSocketMsgType getMsgType(String msgTypeCode) {
        WebSocketMsgType webSocketMsgType = null;
        for (WebSocketMsgType item : WebSocketMsgType.values()) {
            if(msgTypeCode.equals(item.code))
                webSocketMsgType = item;
        }
        return webSocketMsgType;
    }

    public static List<String> getAllMsgType() {
        List<String> msgTypeList = new ArrayList<>();
        for (WebSocketMsgType item : WebSocketMsgType.values()) {
            msgTypeList.add(item.code);
        }
        return msgTypeList;
    }
}
