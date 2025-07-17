package dgb.Mp.Couriel.Dtos;

import dgb.Mp.Couriel.enums.Priority;
import dgb.Mp.Couriel.enums.Status;
import dgb.Mp.File.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourielDtoToUpdate {
  //  private Long id;
    private Priority priority;
    private Status status;
    private String description;
    private String subject;


  private List<UploadFileResponse> uploadedFiles;
  private List<SkippedFileError> skippedFiles;
  private List<DeleteFileResponse> removedFiles;
  private List<UploadFileResponse> courielFiles;
}
