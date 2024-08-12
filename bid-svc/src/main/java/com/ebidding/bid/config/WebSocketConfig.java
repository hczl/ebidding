package com.ebidding.bid.config;

import com.ebidding.bid.websocket.WebSocketServerHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.annotation.Resource;

/**
 * @Description WebSocket 配置
 */
@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    public WebSocketConfig(){
        System.out.println("WebSocket 配置");
    }

    @Resource
    private WebSocketServerHandler webSocketServerHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // msg 为前端请求的地址，前端具体地址组成结构为：ws://127.0.0.1:xxxx/msg
        registry.addHandler(webSocketServerHandler,"msg/").setAllowedOrigins("*");
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
