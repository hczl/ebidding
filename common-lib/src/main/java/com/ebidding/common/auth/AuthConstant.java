package com.ebidding.common.auth;

public class AuthConstant {
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_USER_NAME = "name";
    public static final String CLAIM_ROLE = "role";

    public static final String CLAIM_EXPIRATION_TIME = "exp"; // 添加过期时间字段

    public static final long EXPIRATION_TIME_MS = 7 * 24 * 60 * 60 * 1000; // 1天的过期时间，以毫秒为单位

//    public static final long EXPIRATION_TIME_MS = 1; // 测试过期时间为0


    public static final String X_JWT_ID_HEADER = "X-jwt-id";
    public static final String X_JWT_NAME_HEADER = "X-jwt-name";
    public static final String X_JWT_ROLE_HEADER = "X-jwt-role";

    public static final String TRADER = "TRADER";
    public static final String CLIENT = "CLIENT";


}