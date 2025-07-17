package dgb.Mp.user.Dtos;

import dgb.Mp.Picrures.Dtos.PictureDto;
import dgb.Mp.Role.Dtos.RoleDto;
import dgb.Mp.Role.enums.RoleName;
import dgb.Mp.user.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoResponse {

       private Long id;

        private String nomComplet;



        //private RoleDto role;


        private String plainpassword;

        private String email;

        private String telephone;

        private String quatreChiffres;

        private String Profession;

     //   private PictureDto picture;


        public UserDtoResponse(User newUser, String password) {
                this.id = newUser.getId();
                this.nomComplet = newUser.getNomComplet();
                this.email = newUser.getEmail();
                this.telephone = newUser.getTelephone();
                this.quatreChiffres = newUser.getQuatreChiffres();
                this.Profession = newUser.getProfession();
               // this.picture = PictureDto.fromEntity(newUser.getPicture()); // if you have this converter
                this.plainpassword = password;
        }
}
