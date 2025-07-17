package dgb.Mp.privileges;

import dgb.Mp.privileges.Dtos.PrivilegeDtoToAdd;

import java.util.Set;

public interface PrivilegeService {

    Privilege getPrivilege(Long privilegeId);

    Set<Privilege> getAllPrivileges();

    Privilege getPrivilegeByName(String privilegeName);

    Privilege addPrivilege(PrivilegeDtoToAdd privilegeDtoToAdd);
}
