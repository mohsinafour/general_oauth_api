package com.app.execs;

import static org.junit.jupiter.api.Assertions.*;
import javax.servlet.http.HttpSession;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests for Controller")
public class testController {

	@Autowired
	private MockMvc mockMvc;

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	@DisplayName("Testing Home Page")
	void testHome() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/general/")
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
	}

	@Test
	@DisplayName("Testing Redirection for getting authorization code")
	void testLandGet() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/general/request_data").param("name", "github")
				.accept(MediaType.APPLICATION_JSON);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(HttpStatus.FOUND.value(), result.getResponse().getStatus());
	}

	@Test
	@DisplayName("Test for method extracting authorization code")
	void testAuthoris() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/general/")
				.accept(MediaType.APPLICATION_JSON);
		HttpSession hs = Mockito.mock(HttpSession.class);
		Mockito.when(hs.getAttribute("site")).thenReturn("GOOGLE");
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
	}

	@Test
	@Disabled
	@DisplayName("Test for fetching data")
	void testAuthorised() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/general/general_oauth_redirected")
				.accept(MediaType.APPLICATION_JSON);
		HttpSession hs = Mockito.mock(HttpSession.class);
		ResponseEntity<String> re = new ResponseEntity<String>("this is message", HttpStatus.OK);
		String s = "this is string";
		Mockito.when("access_response_" + hs.getAttribute("site").toString()).thenReturn(s);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
	}

	@SuppressWarnings("unused")
	private static String mapToJson(Object object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(object);
	}

}
