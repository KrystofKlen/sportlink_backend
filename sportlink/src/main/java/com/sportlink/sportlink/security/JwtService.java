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
    public static final Long ACCESS_TOKEN_EXP = TimeUnit.HOURS.toMillis(1L);
    public static final Long REFRESH_TOKEN_EXP = TimeUnit.DAYS.toMillis(20);

    public Long getUserIdFromToken(HttpServletRequest request) throws NumberFormatException, ExpiredJwtException {
        String jwt = getJwtFromRequest(request);
        return extractClaim(jwt, claims -> claims.get("userID", Long.class));
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String extractUsername(String jwt) throws ExpiredJwtException {
        return extractClaim(jwt, Claims::getSubject);
    }

    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) throws ExpiredJwtException{
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    public String generateToken(DTO_Account account, Long exp){
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = account
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);
        claims.put("accountId", account.getId());
        return generateToken(claims, account, exp);
    }

    public String generateToken( Map<String, Object> extraClaims,  DTO_Account account, Long expirationTime) {
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

    public boolean isTokenValid(String jwt, UserDetails userDetails){
        try {
            final String username = extractUsername(jwt);
            boolean usernameOK = username.equals(userDetails.getUsername());
            return usernameOK && !isTokenExpired(jwt);
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

    public List<ROLE> extractRoles(String jwt) {
        Claims claims = extractAllClaims(jwt);
        return claims.get("roles", List.class);
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
