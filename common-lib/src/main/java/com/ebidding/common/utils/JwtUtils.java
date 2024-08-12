package com.ebidding.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ebidding.common.auth.AuthConstant;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
public class JwtUtils {
    private static final String SECRET = "123456";

    public static String SignToken(String id, String name, String role) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8));
            Date expiresAt = new Date(System.currentTimeMillis() + AuthConstant.EXPIRATION_TIME_MS);
            return JWT.create()
                    .withClaim(AuthConstant.CLAIM_USER_ID, id)
                    .withClaim(AuthConstant.CLAIM_USER_NAME, name)
                    .withClaim(AuthConstant.CLAIM_ROLE, role)
                    .withExpiresAt(expiresAt) // 设置过期时间
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            log.error(exception.getMessage());
        }
        return null;
    }

    public static DecodedJWT VerifyToken(String token) {
        //这里会抛出TokenExpiredException，需要在调用的地方捕获
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes(StandardCharsets.UTF_8));
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (TokenExpiredException e) {
            throw e;
        } catch (JWTVerificationException exception){
            // In case of invalid signature or claims, this line will be executed
            log.error(exception.getMessage());
        }
        return null;
    }

}


