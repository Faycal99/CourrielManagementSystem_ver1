package dgb.Mp.Role.Dtos;

import dgb.Mp.Role.enums.RoleName;
import dgb.Mp.privileges.Privilege;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {


    private Long Id;

    private RoleName Name;

    private Set<String> privilegesIdsList;
}
