package com.ebidding.common.auth;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;

public class AuthHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Authorize authorizeAnnotation = method.getAnnotation(Authorize.class);
        if (authorizeAnnotation == null) {
            return true;
        }
        String[] allowedRoles = authorizeAnnotation.value();
        String currentRole = request.getHeader(AuthConstant.X_JWT_ROLE_HEADER);
        if (currentRole == null || !Arrays.asList(allowedRoles).contains(currentRole)) {
            throw new PermissionDenyException();
        }
        return true;
    }
}