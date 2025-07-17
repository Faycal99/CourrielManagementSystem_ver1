package dgb.Mp.Couriel.Dtos;

import dgb.Mp.Couriel.enums.Couriel_Type;
import dgb.Mp.Couriel.enums.Nature;
import dgb.Mp.Couriel.enums.Priority;
import dgb.Mp.Couriel.enums.Status;
import dgb.Mp.Utils.AlgerianMinistries;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourielFilterDto {
  //  private Long id;

    private String courielNumber;
    private String type;

    private String nature;

    private String subject;
    private Status status;

    private Priority priority;

    private LocalDate fromarrivedDate;
    private LocalDate toarrivedDate;

    private LocalDate fromsentDate;
    private LocalDate tosentDate;

    private LocalDate fromreturnDate;
    private LocalDate toreturnDate;





    //   private Long archivedById;

    private Long fromDivisionId;
    private Long fromDirectionId;
    private Long fromSousDirectionId;
    private AlgerianMinistries fromExternal;
    private Long toDivisionId;
    private Long toDirectionId;
    private Long toSousDirectionId;
    private AlgerianMinistries toExternal;

    private String Description;

}
