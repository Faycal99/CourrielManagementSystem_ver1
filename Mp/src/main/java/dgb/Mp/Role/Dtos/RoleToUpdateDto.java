package dgb.Mp.Role.Dtos;

import dgb.Mp.Role.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleToUpdateDto {

    private Long Id;

    private RoleName Name;

    private Set<String> privilegesIdsList;

}
