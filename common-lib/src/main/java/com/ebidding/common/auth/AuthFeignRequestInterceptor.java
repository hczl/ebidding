package com.ebidding.common.auth;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.micrometer.core.instrument.util.StringUtils;

//在服务与服务之间调用时拦截
public class AuthFeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String userId = AuthContext.getUserId();
        String name = AuthContext.getName();
        String role = AuthContext.getRole();
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(name) && StringUtils.isNotBlank(role)) {
            requestTemplate.header(AuthConstant.X_JWT_ID_HEADER, userId);
            requestTemplate.header(AuthConstant.CLAIM_USER_NAME, name);
            requestTemplate.header(AuthConstant.X_JWT_ROLE_HEADER, role);
        }
    }

}
