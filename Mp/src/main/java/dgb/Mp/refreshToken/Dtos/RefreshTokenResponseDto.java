package dgb.Mp.refreshToken.Dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
