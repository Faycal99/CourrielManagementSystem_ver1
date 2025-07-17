package dgb.Mp.Couriel.Dtos;

import dgb.Mp.Couriel.enums.Couriel_Type;
import dgb.Mp.Couriel.enums.Nature;
import dgb.Mp.Couriel.enums.Priority;
import dgb.Mp.Couriel.enums.Status;
import dgb.Mp.Utils.AlgerianMinistries;
import dgb.Mp.validation.ValidCouriel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@ValidCouriel(message = "Check your couriel fields!")
@Builder
public record CreateCourrielRequest(
        String courielNumber,
        String type,
        String nature,
        Status status,
        String subject,
        Priority priority,
        @Schema(description = "arrived date", format = "date", example = "2025-05-27")
        LocalDate arrivedDate,
        @Schema(description = "sent date", format = "date", example = "2025-05-27")

        LocalDate sentDate,
        @Schema(description = "return date", format = "date", example = "2025-05-27")

        LocalDate returnDate,
//        @Schema(description = "saved date", format = "date", example = "2025-05-27")
//
//        LocalDate savedDate,
        Long fromDivisionId,
        Long fromDirectionId,
        Long fromSousDirectionId,
        AlgerianMinistries fromExternal,
        Long toDivisionId,
        Long toDirectionId,
        Long toSousDirectionId,
        AlgerianMinistries toExternal,
       // @Schema(type = "array", format = "binary")
        List<MultipartFile> files,
        String description
) implements CourielValidatble {

    @Override public String getCourielNumber() { return courielNumber; }
    @Override public String getType() { return type; }
    @Override public String getNature() { return nature; }
    @Override public Status getStatus() { return status; }
    @Override public String getSubject() { return subject; }
    @Override public Priority getPriority() { return priority; }
    @Override public LocalDate getArrivedDate() { return arrivedDate; }
    @Override public LocalDate getSentDate() { return sentDate; }
    @Override public LocalDate getReturnDate() { return returnDate; }
  //  @Override public LocalDate getSavedDate() { return savedDate; }

    @Override public Long getFromDivisionId() { return fromDivisionId; }
    @Override public Long getFromDirectionId() { return fromDirectionId; }
    @Override public Long getFromSousDirectionId() { return fromSousDirectionId; }
    @Override public AlgerianMinistries getFromExternal() { return fromExternal; }

    @Override public Long getToDivisionId() { return toDivisionId; }
    @Override public Long getToDirectionId() { return toDirectionId; }
    @Override public Long getToSousDirectionId() { return toSousDirectionId; }
    @Override public AlgerianMinistries getToExternal() { return toExternal; }

    @Override
    public String getDescription() {
        return description;
    }

}