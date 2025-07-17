package dgb.Mp.Direction.Dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DirectionDto {

    private Long id;

    private String directionName;

  //  private Long directorUserId;

 //   private Set<Long> employeesIds;
    private Long divisionId;

}
