package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.authorization.interceptor.IgnoreServiceToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * RestController for other mappings like "*" for fallbacks
 */
@IgnoreServiceToken
@RestController
public class OtherEndpoint {

	/**
	 * Fallback redirects to usage of endpoints on Github
	 *
	 * @param response Given by spring to redirect
	 * @throws IOException is thrown of streams are already closed or else
	 */
	@RequestMapping(value = "*", method = RequestMethod.GET)
	public void getFallback(HttpServletResponse response) throws IOException {
		response.sendRedirect("https://github.com/engelsburg/engelsburg-api/tree/master#endpoint-documentation");
	}

}
