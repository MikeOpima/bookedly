package io.github.mikeopima.bookedly.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowTests {

	@Autowired
	MockMvc mvc;

	private static String uniqueEmail() {
		return "user-" + UUID.randomUUID() + "@example.com";
	}

	private static String registerJson(String email, String password, String name) {
		return """
				{"email":"%s","password":"%s","fullName":"%s"}""".formatted(email, password, name);
	}

	private static String loginJson(String email, String password) {
		return """
				{"email":"%s","password":"%s"}""".formatted(email, password);
	}

	@Test
	void signUpReturnsTokenAndUser() throws Exception {
		String email = uniqueEmail();
		mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
						.content(registerJson(email, "password123", "Mike")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.token").isNotEmpty())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.user.email").value(email))
				.andExpect(jsonPath("$.user.role").value("CUSTOMER"))
				.andExpect(jsonPath("$.user.passwordHash").doesNotExist());
	}

	@Test
	void signUpWithExistingEmailIsRejected() throws Exception {
		String email = uniqueEmail();
		mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
						.content(registerJson(email, "password123", "Mike")))
				.andExpect(status().isCreated());
		// same email, different case
		mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
						.content(registerJson(email.toUpperCase(), "password123", "Mike")))
				.andExpect(status().isConflict());
	}

	@Test
	void signUpValidatesInput() throws Exception {
		mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
						.content(registerJson("not-an-email", "short", "")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.email").exists())
				.andExpect(jsonPath("$.errors.password").exists())
				.andExpect(jsonPath("$.errors.fullName").exists());
	}

	@Test
	void logInThenCallProtectedEndpoint() throws Exception {
		String email = uniqueEmail();
		mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(registerJson(email, "password123", "Mike")));

		String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(loginJson(email, "password123")))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		String token = JsonPath.read(body, "$.token");

		mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value(email))
				.andExpect(jsonPath("$.fullName").value("Mike"));
	}

	@Test
	void wrongPasswordAndUnknownEmailGetTheSameAnswer() throws Exception {
		String email = uniqueEmail();
		mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
				.content(registerJson(email, "password123", "Mike")));

		mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(loginJson(email, "wrong-password")))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.detail").value("Invalid email or password"));

		mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(loginJson(uniqueEmail(), "password123")))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.detail").value("Invalid email or password"));
	}

	@Test
	void protectedEndpointNeedsAValidToken() throws Exception {
		mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
		mvc.perform(get("/api/auth/me").header("Authorization", "Bearer not.a.token"))
				.andExpect(status().isUnauthorized());
	}
}
