package com.amazing.web.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.amazing.OrganizationApp;

/**
 * Organization integration test.
 * 
 * @author hp
 */
@SpringBootTest(classes = OrganizationApp.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class OrganizationIntegrationTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mvc;

	@BeforeEach
	public void setup() throws Exception {
		this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
	}

	@WithMockUser(username = "reader", password = "reader123", roles = "READER")
	@Test
	public void testFetchTree() throws Exception {

		mvc.perform(get("/v1/organizations/6")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(6))
				.andExpect(jsonPath("$.name").value("e")).andExpect(jsonPath("$.height").value(2));
	}
}
