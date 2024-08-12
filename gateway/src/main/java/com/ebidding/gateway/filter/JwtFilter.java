package com.ebidding.gateway.filter;


import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ebidding.common.auth.AuthConstant;
import com.ebidding.common.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import springfox.documentation.builders.PathSelectors;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtFilter extends AbstractGatewayFilterFactory {
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().value();
            // 在这里添加不需要过滤的路径
            List<String> excludePaths = Arrays.asList("/api/v1/account-service/accounts/login","/msg/");
            if (excludePaths.contains(path)) {
                return chain.filter(exchange);
            }
            // 执行过滤器逻辑
            List<String> headers = exchange.getRequest().getHeaders().getOrDefault(HttpHeaders.AUTHORIZATION, new ArrayList<>());
            System.out.println("headers: " + headers);
            if (headers.size() > 0) {
                String authorization = headers.get(0);
                String[] splits = authorization.split("\\s");
                if (splits.length == 2) {
                    String token = splits[1];
                    try {
                        DecodedJWT decodedJWT = JwtUtils.VerifyToken(token);
                        String userId = decodedJWT.getClaim(AuthConstant.CLAIM_USER_ID).asString();
                        String name = decodedJWT.getClaim(AuthConstant.CLAIM_USER_NAME).asString();
                        String role = decodedJWT.getClaim(AuthConstant.CLAIM_ROLE).asString();

                        ServerHttpRequest.Builder builder = exchange.getRequest().mutate()
                                .header(AuthConstant.X_JWT_ID_HEADER, userId)
                                .header(AuthConstant.X_JWT_NAME_HEADER, name)
                                .header(AuthConstant.X_JWT_ROLE_HEADER, role);
                        log.info("请求用户ID ==> {}", userId);
                        ServerHttpRequest modifiedRequest = builder.build();
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    } catch (Exception ex) {
                        String errorMsg = ex instanceof TokenExpiredException ? "Token expired" : "Invalid token";
                        return OnUnAuthorized(exchange, errorMsg); // 自定义返回信息
                    }
                }
            }

            return OnUnAuthorized(exchange, "Missing or invalid authorization header"); // 自定义返回信息
        };
    }


    private Mono<Void> OnUnAuthorized(ServerWebExchange exchange, String errorMessage) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorBody = "{\"error\": \"" + errorMessage + "\"}";
        byte[] errorBytes = errorBody.getBytes();

        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorBytes)));
    }

}
