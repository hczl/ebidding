package com.ebidding.bid.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ebidding.common.utils.WebSocketMessageUtil;
import com.ebidding.common.websocket.UserIdSessionManager;
import com.ebidding.common.websocket.WebSocketSessionManager;
import com.ebidding.common.websocket.model.HandleTextMessageBO;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

/**
 * @Description WebSocket消息处理业务
 */
@Service
@Slf4j
public class MessageTypeService {

//    @Resource
//    private RedisUtils redisUtils;

    /**
     * 处理心跳消息
     * @param handleTextMessageBO
     */
    public void handleHeartPing(HandleTextMessageBO handleTextMessageBO) {
        WebSocketMessageUtil.sendMsgToOne(handleTextMessageBO.getSession(), "ws connect is ok.");
    }

    /**
     * 处理绑定用户ID消息
     * @param handleTextMessageBO
     */
    public void handleBindUserId(HandleTextMessageBO handleTextMessageBO) {
        WebSocketSession session = handleTextMessageBO.getSession();
        JSONObject jsonObject = JSONUtil.parseObj(handleTextMessageBO.getMessage().getPayload());
        Integer userId = jsonObject.getInt("userId");
        if (userId == null) {
            log.error("【WebSocket】userId为空，客户端绑定用户ID失败！");
            WebSocketMessageUtil.sendMsgToOne(session, "userId is null.");
            return;
        }
        // 客户端建立连接,将客户端的session对象加入到WebSocketSessionManager的sessionGroup中,并绑定用户ID
        UserIdSessionManager.add(userId,session);
        WebSocketMessageUtil.sendMsgToOne(session, "bind userId is ok.");
    }

}
