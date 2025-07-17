package dgb.Mp.config.filters;


import dgb.Mp.Utils.JwtUtils;
import dgb.Mp.user.CustomUserDetailService;
import dgb.Mp.user.SecurityUser;
import dgb.Mp.user.User;
import dgb.Mp.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private dgb.Mp.Utils.JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractTokenFromRequest(request);
        logger.debug("Token extracted: {}", token);
//if (token != null && jwtUtils.validateToken(token) && !jwtUtils.isTokenExpired(token) &&
//    SecurityContextHolder.getContext().getAuthentication() == null) {
        if (token != null && jwtUtils.validateToken(token) && !jwtUtils.isTokenExpired(token) &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtUtils.extractUsername(token);
            logger.debug("Username from token: {}", username);

            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
            logger.debug("UserDetails loaded: {}", userDetails);

            if (userDetails != null) {
                // Cast to SecurityUser and get the User entity
                User user = ((SecurityUser) userDetails).getUser();
                logger.debug("User entity loaded: {}", user);

                // Create SecurityUser again if needed, or use userDetails directly
                SecurityUser securityUser = new SecurityUser(user);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.debug("Setting authentication for user: {}", securityUser.getUsername());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authentication set for: {}", securityUser.getUsername());
            } else {
                logger.warn("UserDetails is null for username: {}", username);
            }
        } else {
            logger.debug("Token invalid, expired, or missing: {}", token);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", header);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
