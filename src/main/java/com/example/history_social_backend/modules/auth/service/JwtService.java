package com.example.history_social_backend.modules.auth.service;

import org.springframework.stereotype.Service;

import com.example.history_social_backend.core.exception.AppException;
import com.example.history_social_backend.core.exception.ErrorCode;
import com.example.history_social_backend.modules.user.domain.AppPermission;
import com.example.history_social_backend.modules.user.domain.Role;
import com.example.history_social_backend.modules.user.domain.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    protected String JWT_SECRET;

    @Value("${jwt.valid-duration}")
    protected Long ACCESS_DURATION;

    @Value("${jwt.refresh-duration}")
    protected Long REFRESH_DURATION;

    public String generateAccessToken(User user) throws KeyLengthException {
        return buildToken(user, false);
    }

    public String generateRefreshToken(User user) throws KeyLengthException {
        return buildToken(user, true);
    }

    private String buildToken(User user, boolean isRefresh) throws KeyLengthException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Instant now = Instant.now();

        long duration = isRefresh ? REFRESH_DURATION : ACCESS_DURATION;

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(duration, ChronoUnit.SECONDS)))
                .jwtID(UUID.randomUUID().toString());

        if (isRefresh) {
            builder.claim("type", "refresh");
        } else {
            builder.claim("scope", buildScopeClaim(user));
        }

        builder.claim("id", user.getId().toString());
        builder.claim("status", user.getStatus().name());

        JWTClaimsSet claims = builder.build();

        JWSObject jwsObject = new JWSObject(header, new Payload(claims.toJSONObject()));

        try {
            MACSigner signer = new MACSigner(JWT_SECRET.getBytes());
            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.TOKEN_CREATION_FAILED);
        }
    }

    private String buildScopeClaim(User user) {
        StringJoiner joiner = new StringJoiner(" ");

        for (Role role : user.getRoles()) {
            joiner.add(role.getName());

            for (AppPermission p : role.getAppPermissions()) {
                joiner.add(p.getName());
            }
        }

        return joiner.toString();
    }

    public SignedJWT verifyAccessToken(String token)
            throws JOSEException, ParseException {

        // gọi hàm kiểm tra token có hợp lệ không (đúng cấu trúc, chữ kí hợp lệ)
        SignedJWT jwt = parseAndVerify(token);

        Date expiry = jwt.getJWTClaimsSet().getExpirationTime();

        // kiểm tra token đã hết hạn chưa
        if (expiry == null || expiry.before(new Date()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return jwt;
    }

    public SignedJWT verifyRefreshToken(String token)
            throws JOSEException, ParseException {

        // gọi hàm kiểm tra token có hợp lệ không (đúng cấu trúc, chữ kí hợp lệ)
        SignedJWT jwt = parseAndVerify(token);

        String type = (String) jwt.getJWTClaimsSet().getClaim("type");

        // nếu nó là token chứ không phải refesh thì báo lỗi 
        if (!"refresh".equals(type))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        Date expiry = jwt.getJWTClaimsSet().getExpirationTime();

        if (expiry == null || expiry.before(new Date()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return jwt;
    }

    private SignedJWT parseAndVerify(String token) {
        try {
            if (token == null || token.isBlank()) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            // tách token thành 3 phần header.payload.signature dựa vào dấu chấm
            SignedJWT jwt = SignedJWT.parse(token);

            // tạo đối tượng verifier để kiểm trả chữ kí
            JWSVerifier verifier = new MACVerifier(JWT_SECRET.getBytes());

            // kiểm tra chữ kí có hợp lệ không
            if (!jwt.verify(verifier)) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            return jwt;

        } catch (JOSEException | ParseException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }
}