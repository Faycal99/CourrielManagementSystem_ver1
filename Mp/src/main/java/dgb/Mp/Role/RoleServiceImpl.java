package dgb.Mp.Role;

import dgb.Mp.Role.Dtos.RoleDtoToAdd;
import dgb.Mp.Role.Dtos.RoleToUpdateDto;
import dgb.Mp.Role.enums.RoleName;
import dgb.Mp.generalAdvice.customException.EmptyPrivilegeListException;
import dgb.Mp.generalAdvice.customException.HaveNotPermissionForThat;
import dgb.Mp.generalAdvice.customException.RoleNotFoundException;
import dgb.Mp.privileges.Privilege;
import dgb.Mp.privileges.PrivilegeService;
import dgb.Mp.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {


    private final RoleRepository roleRepository;

    private final PrivilegeService privilegeService;

    @Override
    public Role getRole(Long id) {
        return roleRepository.findById(id).orElseThrow(()-> new RoleNotFoundException("Role Not Found with id: " + id));
    }

//    @Override
//    public Role getRoleByName(String name) {
//
//        return roleRepository.findByName(name).orElseThrow(()-> new RoleNotFoundException("Role Not Found with name: " + name));
//    }


    public Role getRoleByName(String name) {
        RoleName roleName;
        try {
            roleName = RoleName.valueOf(name.toUpperCase()); // Convert String to Enum
        } catch (IllegalArgumentException e) {
            throw new RoleNotFoundException("Invalid Role Name: " + name);
        }
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role Not Found with name: " + name));
    }

    @Override
    public Role AddRole(RoleDtoToAdd roleDto, User user) {
//      String authority = "ROLE_" + user.getRole().getId();

        Role superAdminRole = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Role SUPER_ADMIN not found"));
        System.out.println(superAdminRole.getId());
        if (!user.getRole().getId().equals(1L)) {
            throw new HaveNotPermissionForThat("You, " + user.getUserName() + ", do not have permission to create users. Only SUPER_ADMIN can perform this action.");
        }

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(roleDto.getName().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role name: " + roleDto.getName());
        }
        Role role = roleRepository.findByName(roleName)
                .orElse(new Role());
        if (role.getId() == null) {

            role.setName(RoleName.valueOf(roleDto.getName()));

            Set<Privilege> privileges = roleDto.getPrivilegesIdsList().stream().map(privilegeService::getPrivilegeByName).collect(Collectors.toSet());

            role.setPrivileges(privileges);
        }




       return roleRepository.save(role);

    }

    @Override
    public Role UpdateRolePrivileges(RoleToUpdateDto roleToUpdateDto) {

        Role role = getRole(roleToUpdateDto.getId());

        if(roleToUpdateDto.getPrivilegesIdsList().size()>0) {
            throw new EmptyPrivilegeListException("Privilege list is empty add at least one privilege to that roel to update it !");
        }

        Set<Privilege> privileges = roleToUpdateDto.getPrivilegesIdsList().stream().map(privilegeService::getPrivilegeByName).collect(Collectors.toSet());

        role.setPrivileges(privileges);
        return roleRepository.save(role);

    }

    @Override
    public void DeleteRole(Long id) {
        Role role = getRole(id);
         roleRepository.delete(role);
    }

}
