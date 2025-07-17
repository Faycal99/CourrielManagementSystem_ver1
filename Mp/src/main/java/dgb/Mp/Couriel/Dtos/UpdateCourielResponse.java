package dgb.Mp.Couriel.Dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter


@Setter
public class UpdateCourielResponse {
    private CourielDtoToUpdate updatedCouriel;
    private List<UploadFileResponse> uploadedFiles;
    private List<SkippedFileError> skippedFiles;
    private List<DeleteFileResponse> removedFiles;
}