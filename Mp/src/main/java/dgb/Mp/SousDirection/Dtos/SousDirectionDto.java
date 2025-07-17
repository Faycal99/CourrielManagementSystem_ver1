package dgb.Mp.SousDirection.Dtos;

import dgb.Mp.user.Dtos.UserDto;
import dgb.Mp.user.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class SousDirectionDto
{
    private Long id;

    private String sousDirectionName;

 //   private Long directorUserId;
private Long directionId;

   // private Set<Long> employeesIds;
}
