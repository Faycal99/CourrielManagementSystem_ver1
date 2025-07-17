package dgb.Mp.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class RoutePrivilegeConfig {

    private final Map<String, String> routePrivilegeMap = new HashMap<>();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @PostConstruct
    public void init() {
        // Format: METHOD:ROUTE => PRIVILEGE
        routePrivilegeMap.put("POST:/api/mails", "ADD_COURIEL");
        routePrivilegeMap.put("GET:/api/mails", "READ_COURIEL");
        routePrivilegeMap.put("GET:/api/mails/recent", "READ_COURIEL");
        routePrivilegeMap.put("GET:/api/history/{courielId}", "READ_COURIEL");
        routePrivilegeMap.put("PUT:/api/mails/{id}", "UPDATE_COURIEL");
        routePrivilegeMap.put("DELETE:/api/mail/{id}", "DELETE_COURIEL");


        routePrivilegeMap.put("POST:/auth/createUser", "ADD_USER");
        routePrivilegeMap.put("POST:/auth/createAdmin", "ADD_USER");

        routePrivilegeMap.put("GET:/auth/getAllUsers", "READ_USER");
        routePrivilegeMap.put("GET:/Directions/getByDivisionId", "READ_USER");
        routePrivilegeMap.put("GET:/api/dashboard/summary","ADD_COURIEL");
        routePrivilegeMap.put("GET:/sousDirections/getByDirectionIdAndDivsionId/", "READ_USER");
        routePrivilegeMap.put("PUT:/cm/v1/users/{id}", "UPDATE_USER");
        routePrivilegeMap.put("DELETE:/auth/delete/{id}", "DELETE_USER");

    }

    public Optional<String> getPrivilege(String method, String path) {
        return routePrivilegeMap.entrySet().stream()
                .filter(entry -> {
                    String[] split = entry.getKey().split(":", 2);
                    return split[0].equals(method) && pathMatcher.match(split[1], path);  // ✅ **Pattern matching**
                })
                .map(Map.Entry::getValue)
                .findFirst();
    }
}