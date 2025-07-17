package dgb.Mp.Couriel.Dtos;

import dgb.Mp.Couriel.enums.Priority;
import dgb.Mp.Couriel.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCourielRequest {
    String courielNumber;
    Priority priority;
    Status status;
    String subject;
    String description;
    LocalDate returnDate;
    List<MultipartFile> newFiles;
    List<String> removedFiles;

}
