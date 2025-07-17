package dgb.Mp.Utils;

import dgb.Mp.privileges.Privilege;
import dgb.Mp.user.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration.access}")
    private long accessTokenExpiration;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpiration;

    // Generate access token
    public String generateAccessToken(User user) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured");
        }
        String role = user.getRole().getName().name();
        List<String> privileges = user.getRole().getPrivileges().stream()
                .map(privilege -> privilege.getName().toString())
                .collect(Collectors.toList());

// Add organization context and we will use this to differ between normal
// user , admin (division) and admin (direction)
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("privileges", privileges);

        if (user.getDivision() != null) {
            claims.put("divisionId", user.getDivision().getId());
        } else if (user.getDirection() != null) {
            claims.put("directionId", user.getDirection().getId());
        } else if (user.getSouDirection() != null) {
            claims.put("souDirectionId", user.getSouDirection().getId());
        }

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();


        /*   if (secretKey == null || secretKey.trim().isEmpty()) {
        throw new IllegalStateException("JWT secret key is not configured");
    }

    String role = user.getRole().getName().name();
    List<String> privileges = user.getRole().getPrivileges().stream()
            .map(privilege -> privilege.getName().toString())
            .collect(Collectors.toList());

    // Add organization context (only one of these will be non-null based on your logic)
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);
    claims.put("privileges", privileges);

    if (user.getDivision() != null) {
        claims.put("divisionId", user.getDivision().getId());
    } else if (user.getDirection() != null) {
        claims.put("directionId", user.getDirection().getId());
    } else if (user.getSouDirection() != null) {
        claims.put("souDirectionId", user.getSouDirection().getId());
    }

    return Jwts.builder()
            .setClaims(claims)
            .setSubject(user.getEmail())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();*/
    }

    // Generate refresh token
    public String generateRefreshToken(User user) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured");
        }

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Extract claims from the token
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract authorities (roles) from token
    public String extractAuthorities(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // Get expiration date
    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaims(token).getExpiration();
            return expiration == null || expiration.before(new Date());
        } catch (Exception e) {
            // Log the error for debugging purposes
            System.err.println("Failed to extract expiration: " + e.getMessage());
            return true; // Treat invalid tokens as expired
        }
    }
    public String extractRefreshToken(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        System.out.println("Auth Header: " + authHeader); // Debug line

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                System.out.println("Cookie: " + cookie.getName() + "=" + cookie.getValue()); // Debug line
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        throw new RuntimeException("Refresh token not found in header or cookies");// not found
    }
}

