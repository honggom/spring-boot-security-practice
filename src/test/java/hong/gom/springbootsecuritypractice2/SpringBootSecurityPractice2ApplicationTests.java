package hong.gom.springbootsecuritypractice2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootSecurityPractice2ApplicationTests {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@LocalServerPort
	int port;

	RestTemplate client = new RestTemplate();

	private String greetingUrl(){
		return "http://localhost:"+port+"/greeting";
	}

	@DisplayName("1. 인증 실패")
	@Test
	void test1(){

		HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
			client.getForObject(greetingUrl(), String.class);
		});

		assertEquals(401, exception.getRawStatusCode());

	}

	@DisplayName("2. 인증 성공")
	@Test
	void test2(){
		HttpHeaders headres = new HttpHeaders();
		headres.add(HttpHeaders.AUTHORIZATION, "Basic "+ Base64.getEncoder().encodeToString(
				"user1:1111".getBytes()
		));
		HttpEntity entity = new HttpEntity(null, headres);
		ResponseEntity<String> response = client.exchange(greetingUrl(), HttpMethod.GET, entity, String.class);

		logger.info("test2 start...");
		logger.info("test2 start...");
		logger.info("response.getBody() : " + response.getBody());

		assertEquals("hello", response.getBody());
	}

	@DisplayName("3. 인증 성공2")
	@Test
	void test3() {
		TestRestTemplate testClient = new TestRestTemplate("user1", "1111");
		String response = testClient.getForObject(greetingUrl(), String.class);

		assertEquals("hello", response);
	}

	@DisplayName("4. POST 인증")
	@Test
	void test4() {
		TestRestTemplate testClient = new TestRestTemplate("user1", "1111");
		ResponseEntity<String> response = testClient.postForEntity(greetingUrl(), "hong", String.class);

		// post 요청 시에는 cofig에서  http.csrf().disable() 해줘야 됨
		assertEquals("hello hong", response.getBody());
	}
}
