package dgb.Mp.user.Dtos;

import dgb.Mp.Picrures.Dtos.PictureDto;
import dgb.Mp.Picrures.Picture;
import dgb.Mp.Role.Dtos.RoleDto;
import dgb.Mp.Role.Role;
import dgb.Mp.Role.enums.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String userName;



    private RoleDto role;


    private String password;

    private String email;

    private PictureDto picture;

}
