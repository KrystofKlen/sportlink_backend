package com.sportlink.sportlink.security;

import com.sportlink.sportlink.account.ROLE;
import com.sportlink.sportlink.account.account.DTO_Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.key}")
    private String secretKey;
    public static final Long ACCESS_TOKEN_STANDARD_EXP = TimeUnit.HOURS.toMillis(1);
    public static final Long REFRESH_TOKEN_STANDARD_EXP = TimeUnit.DAYS.toMillis(20);
    public static final Long ACCESS_TOKEN_ADMIN_EXP = TimeUnit.MINUTES.toMillis(5);
    public static final Long ACCESS_TOKEN_DEVICE_EXP = TimeUnit.DAYS.toMillis(1);
    public static final Long REFRESH_TOKEN_DEVICE_EXP = TimeUnit.DAYS.toMillis(30);

    public String extractUsername(String jwt) throws ExpiredJwtException {
        return extractClaim(jwt, Claims::getSubject);
    }

    public String extractTokenType(String jwt) throws ExpiredJwtException {
        return extractClaim(jwt, claims -> claims.get("token_type", String.class));
    }

    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) throws ExpiredJwtException{
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    public String generateToken(DTO_Account account, TOKEN_TYPE type) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = account
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);
        claims.put("accountId", account.getId());
        claims.put("token_type", type.toString());

        switch (type){
            case ACCESS: return generateAccessToken(claims, account);
            case REFRESH: return generateRefreshToken(claims, account);
            default: throw  new IllegalArgumentException();
        }
    }

    public String generateAccessToken(Map<String, Object> claims,  DTO_Account account){
        ROLE role = account.getRole();

        if(role.equals(ROLE.ROLE_USER) || role.equals(ROLE.ROLE_COMPANY)){
            return buildToken(claims, account, ACCESS_TOKEN_STANDARD_EXP);
        } else if (role.equals(ROLE.ROLE_ADMIN)) {
            return buildToken(claims, account, ACCESS_TOKEN_ADMIN_EXP);
        } else if (role.equals(ROLE.ROLE_LOCATION_DEVICE)){
            return buildToken(claims, account, ACCESS_TOKEN_DEVICE_EXP);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String generateRefreshToken(Map<String, Object> claims,  DTO_Account account){
        ROLE role = account.getRole();
        if (role.equals(ROLE.ROLE_LOCATION_DEVICE)){
            return buildToken(claims, account, REFRESH_TOKEN_DEVICE_EXP);
        } else if(role.equals(ROLE.ROLE_USER) || role.equals(ROLE.ROLE_COMPANY)) {
            return buildToken(claims, account, REFRESH_TOKEN_STANDARD_EXP);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String buildToken(Map<String, Object> extraClaims, DTO_Account account, Long expirationTime) {
        return Jwts
                .builder()
                .setId(account.getId().toString())
                .setClaims(extraClaims)
                .setSubject(account.getUsername())
                .setIssuedAt(new Date((System.currentTimeMillis())))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails, TOKEN_TYPE expectedType){
        try {
            // username
            final String username = extractUsername(jwt);
            boolean usernameOK = username.equals(userDetails.getUsername());
            if(!usernameOK){
                return false;
            }
            // token expiration
            boolean tokenExpired = isTokenExpired(jwt);
            if(tokenExpired){
                return false;
            }
            // token type matches
            String tokenType = extractTokenType(jwt);
            boolean typeOK =  tokenType.equals(expectedType.toString());
            if(!typeOK){
                return false;
            }

            return true;
        }catch (Exception e){
            return false;
        }
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    public Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    private Claims extractAllClaims(String jwt) throws ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
