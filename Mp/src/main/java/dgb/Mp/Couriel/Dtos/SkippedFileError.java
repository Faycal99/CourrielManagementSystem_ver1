package dgb.Mp.Couriel.Dtos;

import lombok.Builder;

@Builder
public record SkippedFileError(String fileName, String reason) {
}
