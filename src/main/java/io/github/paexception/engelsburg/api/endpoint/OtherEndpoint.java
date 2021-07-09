package io.github.paexception.engelsburg.api.endpoint;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * RestController for other mappings like "*" for fallbacks.
 */
@RestController
public class OtherEndpoint {

	/**
	 * Fallback redirects to usage of endpoints on Github.
	 *
	 * @param response Given by spring to redirect
	 * @throws IOException is thrown of streams are already closed or else
	 */
	@RequestMapping("*")
	public void getFallback(HttpServletResponse response) throws IOException {
		response.sendRedirect("https://github.com/engelsburg/engelsburg-api/tree/master#endpoint-documentation");
	}

}
