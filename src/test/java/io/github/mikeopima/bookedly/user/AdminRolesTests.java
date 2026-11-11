package io.github.mikeopima.bookedly.user;

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

@SpringBootTest(properties = {
		"bookedly.admin.email=admin@test.local",
		"bookedly.admin.password=admin-test-password"
})
@AutoConfigureMockMvc
class AdminRolesTests {

	@Autowired
	MockMvc mvc;

	private static String uniqueEmail() {
		return "user-" + UUID.randomUUID() + "@example.com";
	}

	private String register(String email) throws Exception {
		return mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"%s","password":"password123","fullName":"Test User"}""".formatted(email)))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
	}

	private String loginToken(String email, String password) throws Exception {
		String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"%s","password":"%s"}""".formatted(email, password)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		return JsonPath.read(body, "$.token");
	}

	private String adminToken() throws Exception {
		return loginToken("admin@test.local", "admin-test-password");
	}

	@Test
	void firstAdminIsCreatedAtStartup() throws Exception {
		mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email":"admin@test.local","password":"admin-test-password"}"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.user.role").value("ADMIN"));
	}

	@Test
	void customersCannotUseAdminEndpoints() throws Exception {
		String body = register(uniqueEmail());
		String customerToken = JsonPath.read(body, "$.token");
		Integer customerId = JsonPath.read(body, "$.user.id");

		mvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + customerToken))
				.andExpect(status().isForbidden());
		mvc.perform(post("/api/admin/users/{id}/promote", customerId)
						.header("Authorization", "Bearer " + customerToken))
				.andExpect(status().isForbidden());
	}

	@Test
	void adminEndpointsNeedAToken() throws Exception {
		mvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());
	}

	@Test
	void adminCanListUsers() throws Exception {
		String email = uniqueEmail();
		register(email);
		mvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + adminToken()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.email == '%s')].role".formatted(email)).value("CUSTOMER"))
				.andExpect(jsonPath("$[0].passwordHash").doesNotExist());
	}

	@Test
	void adminCanPromoteACustomer() throws Exception {
		String email = uniqueEmail();
		String body = register(email);
		String oldToken = JsonPath.read(body, "$.token");
		Integer id = JsonPath.read(body, "$.user.id");

		mvc.perform(post("/api/admin/users/{id}/promote", id).header("Authorization", "Bearer " + adminToken()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.role").value("ADMIN"));

		// The old token was issued while they were a CUSTOMER, so it still says CUSTOMER
		mvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + oldToken))
				.andExpect(status().isForbidden());

		// After logging in again they get an ADMIN token
		mvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + loginToken(email, "password123")))
				.andExpect(status().isOk());
	}

	@Test
	void promotingAnUnknownUserIs404() throws Exception {
		mvc.perform(post("/api/admin/users/{id}/promote", 999999).header("Authorization", "Bearer " + adminToken()))
				.andExpect(status().isNotFound());
	}
}
