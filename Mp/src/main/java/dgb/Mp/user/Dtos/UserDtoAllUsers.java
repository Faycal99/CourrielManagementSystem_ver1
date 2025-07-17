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
public class UserDtoAllUsers {
private Long id;
    private String nomComplet;

    private String email;

    private String telephone;

   @Enumerated(EnumType.STRING)
    private RoleName role;

    private String divisionId;//string     // Optional for validation
    private String directionId;//string
    private String souDirectionId;//string

    private String Profession;

    private String quatreChiffres;

}
