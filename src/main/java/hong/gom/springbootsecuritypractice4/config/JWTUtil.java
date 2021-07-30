package hong.gom.springbootsecuritypractice4.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.Instant;

import com.auth0.jwt.interfaces.DecodedJWT;
import hong.gom.springbootsecuritypractice4.dto.SpUser;

public class JWTUtil {

    private static final Algorithm ALGORITHM = Algorithm.HMAC256("hong");
    private static final long AUTH_TIME = 20 * 60;
    private static final long REFRESH_TIME = 60 * 60 * 24 * 7;

    public static String makeAuthToken(SpUser user){
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("exp", Instant.now().getEpochSecond() + AUTH_TIME)
                .sign(ALGORITHM);
    }

    public static String makeRefreshToken(SpUser user){
        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("exp", Instant.now().getEpochSecond() + REFRESH_TIME)
                .sign(ALGORITHM);
    }

    public static VerifyResult verity(String token){
        try{
        DecodedJWT verify = JWT.require(ALGORITHM).build().verify(token);
            return VerifyResult.builder()
                    .success(true)
                    .username(verify.getSubject())
                    .build();
        } catch (Exception e){
            DecodedJWT decode = JWT.decode(token);
            return VerifyResult.builder()
                    .success(false)
                    .username(decode.getSubject())
                    .build();
        }
    }

}
