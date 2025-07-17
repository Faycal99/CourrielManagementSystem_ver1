package dgb.Mp.Couriel.Dtos;

import dgb.Mp.Couriel.enums.Couriel_Type;
import dgb.Mp.Couriel.enums.Nature;
import dgb.Mp.Couriel.enums.Priority;
import dgb.Mp.Couriel.enums.Status;
import dgb.Mp.File.File;
import dgb.Mp.History.Dto.HistoryDto;
import dgb.Mp.Utils.AlgerianMinistries;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourielDto implements CourielValidatble {
   // private Long id;

    private String courielNumber;
    private String type;

    private String nature;

    private String subject;
    private Priority priority;
    private Status status;


    private LocalDate arrivedDate;
    private LocalDate sentDate;
    private LocalDate returnDate;
  //  private LocalDate savedDate;



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

    private List<HistoryDto> historyList;
    private List<File> courielFiles;
    private String courrielPath;


}
