package net.hauntedstudio.Authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import net.hauntedstudio.Authentication.entity.Token;
import net.hauntedstudio.Authentication.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final String SECRET = "";

    @Autowired
    private TokenRepository tokenRepository;

    // Token validity period in days
    private static final int TOKEN_VALIDITY_DAYS = 7;

    public String generateToken(String uuid, UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList()));
        Date expirationDate = calculateExpirationDate();
        String token = createToken(claims, uuid, expirationDate);
        saveToken(token, userDetails.getUsername(), expirationDate);
        return token;
    }

    private String createToken(Map<String, Object> claims, String uuid, Date expirationDate) {
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(uuid)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private void saveToken(String token, String uuid, Date expirationDate) {
        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setUuid(uuid);
        tokenEntity.setExpirationDate(expirationDate);
        tokenRepository.save(tokenEntity);
    }

    // Calculate expiration date by adding the validity period to the current date
    private Date calculateExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, TOKEN_VALIDITY_DAYS); // Add 7 days to the current date
        return calendar.getTime();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUuid(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String uuid = extractUuid(token);
        return (uuid.equals(((UserInfoDetails) userDetails).getUuid()) && !isTokenExpired(token) && isTokenStored(token));
    }

    private boolean isTokenStored(String token) {
        return tokenRepository.findByToken(token).isPresent();
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }
}