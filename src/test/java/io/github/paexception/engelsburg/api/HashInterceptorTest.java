package io.github.paexception.engelsburg.api;

import io.github.paexception.engelsburg.api.controller.InformationController;
import io.github.paexception.engelsburg.api.endpoint.InformationEndpoint;
import io.github.paexception.engelsburg.api.endpoint.dto.response.GetClassesResponseDTO;
import io.github.paexception.engelsburg.api.spring.interceptor.HashInterceptor;
import io.github.paexception.engelsburg.api.util.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testing the {@link HashInterceptor} via {@link InformationEndpoint}
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {InformationEndpoint.class, InformationController.class, HashInterceptor.class})
public class HashInterceptorTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private InformationController informationController;

	@Test
	public void testHashInterceptor() throws Exception {
		Mockito.when(this.informationController.getCurrentClasses())
				.thenReturn(Result.of(new GetClassesResponseDTO(new String[]{"5c"})));

		MvcResult result = this.mockMvc.perform(get("/info/classes").accept(MediaType.APPLICATION_JSON)).andReturn();
		this.mockMvc.perform(get("/info/classes")
				.accept(MediaType.APPLICATION_JSON)
				.header("Hash", result.getResponse().getHeader("Hash")))
				.andExpect(status().isNotModified())
				.andReturn();

		Mockito.verify(this.informationController, Mockito.times(2)).getCurrentClasses();
	}

}
