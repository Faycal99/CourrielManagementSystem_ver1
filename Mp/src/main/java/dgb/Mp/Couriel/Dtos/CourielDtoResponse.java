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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourielDtoResponse {

    private String courielNumber;
    private Couriel_Type type;

    private Nature nature;

    private String subject;
    private Priority priority;
    private Status status;


    private LocalDate arrivedDate;
    private LocalDate sentDate;
    private LocalDate returnDate;
    //  private LocalDate savedDate;



    //   private Long archivedById;

    private String fromDivisionId;
    private String fromDirectionId;
    private String fromSousDirectionId;
    private AlgerianMinistries fromExternal;
    private String toDivisionId;
    private String toDirectionId;
    private String toSousDirectionId;
    private AlgerianMinistries toExternal;

    private String Description;

    private List<HistoryDto> historyList;
    private List<File> courielFiles;
    private String courrielPath;


    public String getCourielNumber() {
        // Convert underscores to slashes for display
        return courielNumber != null ? courielNumber.replace("_", "/") : null;
    }
    String prettyName(String name) {
        if (name == null) return null;
        String[] stopWords = {"de", "des", "du", "la", "le", "les", "pour", "et", "au"};
        String[] words = name.split("\\s+");
        StringBuilder code = new StringBuilder();
        for (String word : words) {
            if (!Arrays.asList(stopWords).contains(word.toLowerCase()) && !word.isBlank()) {
                code.append(Character.toUpperCase(word.charAt(0)));
            }
        }
        return "(" + code + ") " + name;
    }



}
