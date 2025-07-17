package dgb.Mp.Couriel.Dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateCourrielResponse(
        String courielNumber,
        List<UploadFileResponse> uploadedFiles,
        List<SkippedFileError> skippedFiles
) {}
