package hong.gom.springbootsecuritypractice4;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

@SpringBootTest
class SpringBootSecurityPractice4ApplicationTests {

	private void printToken(String token){
		String[] tokens = token.split("\\.");
		System.out.println("header : "+new String(Base64.getDecoder().decode(tokens[0])));
		System.out.println("body : "+new String(Base64.getDecoder().decode(tokens[1])));
	}

	@DisplayName("1. jjwt 테스트")
	@Test
	void test1(){
		String okta_token = Jwts.builder().addClaims(
				Map.of("name", "hong", "price", 1000)
		).signWith(SignatureAlgorithm.HS256, "hong")
				.compact();
		System.out.println(okta_token);
		printToken(okta_token);
	}

	@DisplayName("2. java-jwt 테스트")
	@Test
	void test2(){
		String oauth0_token = JWT.create().withClaim("name", "hong").withClaim("price", 1000)
				.sign(Algorithm.HMAC256("hong"));
		System.out.println(oauth0_token);
		printToken(oauth0_token);

		DecodedJWT verified = JWT.require(Algorithm.HMAC256("hong")).build().verify(oauth0_token);
		System.out.println(verified.getClaims());
	}

	@DisplayName("3. 만료시간 테스트")
	@Test
	void test3() throws InterruptedException {
		final Algorithm AL = Algorithm.HMAC256("hong");
		String token = JWT.create().withSubject("a1234").withExpiresAt(new Date(System.currentTimeMillis()+1000))
				.sign(AL);

		Thread.sleep(2000);
		DecodedJWT verity = JWT.require(AL).build().verify(token);
		System.out.println(verity.getClaims());
	}


}
