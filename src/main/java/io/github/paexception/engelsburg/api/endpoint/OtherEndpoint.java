/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.endpoint;

import io.github.paexception.engelsburg.api.spring.rate_limiting.IgnoreGeneralRateLimit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller for other mappings like "*" for fallbacks.
 */
@Controller
public class OtherEndpoint {

	/**
	 * Fallback redirects to usage of endpoints on Github.
	 *
	 * @param response Given by spring to redirect
	 * @throws IOException is thrown of streams are already closed or else
	 */
	@IgnoreGeneralRateLimit
	@RequestMapping("*")
	public void getFallback(HttpServletResponse response) throws IOException {
		response.sendRedirect("https://github.com/engelsburg/engelsburg-api/tree/master#endpoint-documentation");
	}

	/**
	 * Returns data policy.
	 *
	 * @return data policy as html page
	 */
	@IgnoreGeneralRateLimit
	@RequestMapping("/data_policy")
	public Object dataPolicy() {
		return "data_policy";
	}
}
