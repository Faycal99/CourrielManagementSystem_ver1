package dgb.Mp.privileges;

import dgb.Mp.Role.Role;
import dgb.Mp.Role.RoleRepository;
import dgb.Mp.Role.enums.RoleName;
import dgb.Mp.privileges.enums.privilegeEnum;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@Component
//@RequiredArgsConstructor
//public class PrivilegeInitializer {
//
//    private final PrivilegeRepository privilegeRepository;
//    private final RoleRepository roleRepository;
//
//    @PostConstruct
//    public void syncPrivilegesWithEnum() {
//        Set<privilegeEnum> existingPrivilegeEnums = privilegeRepository.findAll().stream()
//                .map(Privilege::getName)
//                .collect(Collectors.toSet());
//
//        List<Privilege> newPrivileges = Arrays.stream(privilegeEnum.values())
//                .filter(p -> !existingPrivilegeEnums.contains(p))
//                .map(p -> {
//                    Privilege priv = new Privilege();
//                    priv.setName(p);
//                    return priv;
//                }).toList();
//
//        if (!newPrivileges.isEmpty()) {
//            privilegeRepository.saveAll(newPrivileges);
//        }
//
//        // Assign all privileges to ADMIN role
//        Role adminRole = roleRepository.findByName(RoleName.valueOf("ADMIN"))
//                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
//
//        Set<Privilege> allPrivileges = new HashSet<>(privilegeRepository.findAll());
//        adminRole.setPrivileges(allPrivileges);
//        roleRepository.save(adminRole);
//    }
//}
