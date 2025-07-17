package dgb.Mp.Role;

import dgb.Mp.Role.Dtos.RoleDto;
import dgb.Mp.Role.Dtos.RoleDtoToAdd;
import dgb.Mp.Role.Dtos.RoleToUpdateDto;
import dgb.Mp.user.User;

public interface RoleService {

    Role getRole(Long id);
    Role getRoleByName(String name);

   /* Role AddRole(RoleDtoToAdd roleDto);*/

    Role AddRole(RoleDtoToAdd roleDto, User user);

    Role UpdateRolePrivileges(RoleToUpdateDto roleToUpdateDto);

    void DeleteRole(Long id);
}
