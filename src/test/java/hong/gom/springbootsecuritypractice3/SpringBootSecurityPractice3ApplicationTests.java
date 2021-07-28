package hong.gom.springbootsecuritypractice3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hong.gom.springbootsecuritypractice3.student.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootSecurityPractice3ApplicationTests {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@LocalServerPort
	int port;

	RestTemplate restTemplate = new RestTemplate();
	
	@DisplayName("1. 학생 조사")
	@Test
	void test1() throws JsonProcessingException {
		String url = format("http://localhost:%d/api/teacher/students", port);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.AUTHORIZATION, "Basic "+ Base64.getEncoder().encodeToString(
				"choi:1".getBytes()
		));

		HttpEntity<String> entity = new HttpEntity<>("", httpHeaders);
		ResponseEntity<String> reponse = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		List<Student> studentList = new ObjectMapper().readValue(reponse.getBody(),
				new TypeReference<List<Student>>() {
				});

		logger.info("test 1 start...");
		logger.info("test 1 start...");
		logger.info("studentList : "+studentList);

		assertEquals(3, studentList.size());

	}
}
