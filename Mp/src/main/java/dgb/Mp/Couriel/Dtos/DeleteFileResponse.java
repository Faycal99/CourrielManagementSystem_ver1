package dgb.Mp.Couriel.Dtos;

import lombok.Builder;

@Builder
public record DeleteFileResponse(String courrielNumber,
                                 String courrielPath,
                                 String fileName) {
}

