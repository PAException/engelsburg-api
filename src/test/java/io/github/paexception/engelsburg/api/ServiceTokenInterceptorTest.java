package io.github.paexception.engelsburg.api;

import io.github.paexception.engelsburg.api.authorization.interceptor.ServiceTokenInterceptor;
import io.github.paexception.engelsburg.api.controller.SubstituteController;
import io.github.paexception.engelsburg.api.database.repository.SubstituteRepository;
import io.github.paexception.engelsburg.api.endpoint.SubstituteEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing the {@link ServiceTokenInterceptor}
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = {SubstituteController.class, SubstituteEndpoint.class})
public class ServiceTokenInterceptorTest {

	private final ServiceTokenInterceptor serviceTokenInterceptor = new ServiceTokenInterceptor("test_token");
	@Autowired
	private RequestMappingHandlerMapping handlerMapping;
	@MockBean
	private SubstituteRepository substituteRepository;

	@Test
	public void testServiceToken() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/substitute");
		request.setMethod("GET");
		assertEquals(401, this.mockInterceptor(request));

		request = new MockHttpServletRequest();
		request.setRequestURI("/substitute");
		request.setMethod("GET");
		request.addHeader("ServiceToken", "test_token");
		assertEquals(404, this.mockInterceptor(request));
	}

	private int mockInterceptor(MockHttpServletRequest request) throws Exception {
		MockHttpServletResponse response = new MockHttpServletResponse();

		HandlerExecutionChain handlerExecutionChain = this.handlerMapping.getHandler(request);
		assert handlerExecutionChain != null;
		this.serviceTokenInterceptor.preHandle(request, response, handlerExecutionChain.getHandler());

		return response.getStatus();
	}

}
