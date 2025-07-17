package dgb.Mp.user.Dtos;

import dgb.Mp.Role.enums.RoleName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoToUpdate {


    private String nomComplet;

    private String email;

    private String telephone;

    @Enumerated(EnumType.STRING)
    private RoleName role;


}
