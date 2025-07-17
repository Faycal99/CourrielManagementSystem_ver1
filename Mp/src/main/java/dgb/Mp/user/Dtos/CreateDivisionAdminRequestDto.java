package dgb.Mp.user.Dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateDivisionAdminRequestDto {

    private String email;
    private String name;
    private String username;
    private String password;
    private Long divisionId;
    private String quatreChiffres;
    private String profession;
}
