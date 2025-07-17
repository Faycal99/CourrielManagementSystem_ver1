package dgb.Mp.Couriel.Dtos;

import lombok.Builder;

@Builder
public record UploadFileResponse(     String fileName,
                                      String filePath,
                                      String fileSize ) {
}
