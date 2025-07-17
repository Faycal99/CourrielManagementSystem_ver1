package dgb.Mp.Couriel.Dtos;

import lombok.Builder;

@Builder
public record ApiResponse<T>(String message, T data) {
}
