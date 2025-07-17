package dgb.Mp.refreshToken;

public interface RefreshTokenService {

    RefreshToken getRefreshTokenbyToekn(String toekn);

    RefreshToken saveRefreshToken(RefreshToken refreshToken);


}
