package dgb.Mp.Couriel.Dtos;

import dgb.Mp.Couriel.enums.Couriel_Type;
import dgb.Mp.Couriel.enums.Priority;
import dgb.Mp.Couriel.enums.Nature;
import dgb.Mp.Couriel.enums.Status;
import dgb.Mp.Utils.AlgerianMinistries;
import dgb.Mp.validation.ValidCouriel;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ValidCouriel(message = "Check your couriel fields!")
public class CourielDtoToAdd implements CourielValidatble {

    private String courielNumber;
    private String type;

    private String nature;
    private Status status;

    private String subject;
    private Priority priority;

    private LocalDate arrivedDate;
    private LocalDate sentDate;
    private LocalDate returnDate;
   // private LocalDate savedDate;


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
  //  private List<HistoryDto> historyList;
}
