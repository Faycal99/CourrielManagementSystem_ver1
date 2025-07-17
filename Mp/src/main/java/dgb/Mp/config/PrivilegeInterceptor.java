package dgb.Mp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;
@Component
public class PrivilegeInterceptor implements HandlerInterceptor {
    private final RoutePrivilegeConfig routePrivilegeService;

    public PrivilegeInterceptor(RoutePrivilegeConfig routePrivilegeService) {
        this.routePrivilegeService = routePrivilegeService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        String normalizedUri = normalizePath(uri);
        Optional<String> requiredPrivilegeOpt = routePrivilegeService.getPrivilege(method, normalizedUri);

        if (requiredPrivilegeOpt.isEmpty()) {
            return true; // No privilege required
        }

        String requiredPrivilege = requiredPrivilegeOpt.get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            // ✅ Debugging: Print out all authorities the user has
            System.out.println("User is authenticated: " + auth.getName());
            System.out.println("Required privilege: " + requiredPrivilege);
            auth.getAuthorities().forEach(a ->
                    System.out.println("Authority: " + a.getAuthority())
            );

            for (GrantedAuthority authority : auth.getAuthorities()) {
                if (authority.getAuthority().equals(requiredPrivilege)) {
                    return true; // ✅ Access granted
                }
            }
        }

        // ❌ Access denied
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write("Forbidden: Missing required privilege " + requiredPrivilege);
        return false;
    }

    // Optional: Normalize path variables to match the route map in RoutePrivilegeConfig
    private String normalizePath(String uri) {
        // Example: Replace numeric IDs with {id}
        return uri.replaceAll("/\\d+", "/{id}");
    }
}
