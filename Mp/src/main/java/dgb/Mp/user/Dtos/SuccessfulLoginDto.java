package dgb.Mp.user.Dtos;

import dgb.Mp.Role.Dtos.RoleDto;
import dgb.Mp.Role.Dtos.RoleDtoName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessfulLoginDto {


        private String accessToken;

        private String refreshToken;

        private String role;

        private Long divisionId;
        private Long directionId;
        private Long sousdirectionId;

        // private UserDto user;
        // getters and setters
}




