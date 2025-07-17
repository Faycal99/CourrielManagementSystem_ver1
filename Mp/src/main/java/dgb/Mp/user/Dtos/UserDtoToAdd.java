package dgb.Mp.user.Dtos;

import dgb.Mp.Role.Role;
import dgb.Mp.Role.enums.RoleName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoToAdd {


    private String nomComplet;

    private String email;

    private String telephone;

//    @Enumerated(EnumType.STRING)
//    private RoleName role;

    private Long divisionId;     // Optional for validation
    private Long directionId;
    private Long souDirectionId;
    private String quatreChiffres;

    private String profession;

 //   private RoleName role;

}
