package dgb.Mp.refreshToken;

import dgb.Mp.generalAdvice.customException.RefreshTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public RefreshToken getRefreshTokenbyToekn(String toekn) {
        return refreshTokenRepository.findByRefreshToken(toekn).orElseThrow(()-> new RefreshTokenNotFoundException("refresh token not found "));
    }

    @Override
   public RefreshToken saveRefreshToken(RefreshToken newToken) {
    Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(newToken.getUser());

    if (existingTokenOpt.isPresent()) {
        RefreshToken existingToken = existingTokenOpt.get();
        existingToken.setRefreshToken(newToken.getRefreshToken());
        existingToken.setExpiresAt(newToken.getExpiresAt());
        existingToken.setCreatedAt(Date.from(Instant.now()));
        refreshTokenRepository.save(existingToken);
    } else {
        newToken.setCreatedAt(Date.from(Instant.now()));
        refreshTokenRepository.save(newToken);
    }
        return newToken;
    }
}
