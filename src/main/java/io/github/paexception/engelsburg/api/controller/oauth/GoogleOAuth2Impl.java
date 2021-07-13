package io.github.paexception.engelsburg.api.controller.oauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonParser;
import io.github.paexception.engelsburg.api.util.Environment;
import io.github.paexception.engelsburg.api.util.Result;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller to handle google oauth2.
 */
@Component
public class GoogleOAuth2Impl extends OAuthHandler {

	private String oauth2Request;

	/**
	 * Build the Oauth request link.
	 */
	@Bean
	public void buildOAuthRequest() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("client_id", Environment.GOOGLE_CLIENT_ID);
		parameters.put("redirect_uri", this.getRedirectUri());
		parameters.put("response_type", "code");
		parameters.put("state", "{token}");
		parameters.put("scope", "https://www.googleapis.com/auth/userinfo.email");

		StringBuilder builder = new StringBuilder();
		builder.append("https://accounts.google.com/o/oauth2/v2/auth?");
		parameters.forEach((k, v) -> builder.append(k).append("=").append(v).append("&"));
		builder.delete(builder.length() - 1, builder.length());

		this.oauth2Request = builder.toString();
	}

	/**
	 * Resolve the response of the google auth server.
	 *
	 * <b>State</b> is to verify the request.
	 * Email of user is in jwt.
	 * Return null on any error.
	 *
	 * @param request  sent by the server
	 * @param response given by spring
	 * @return email or null if any error.
	 */
	@Override
	public String resolveOAuthResponse(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (this.verifyAndDeleteToken(request.getParameter("state"))) {
				DecodedJWT jwt = JWT.decode(this.getJWT(request.getParameter("code")));
				return jwt.getClaim("email").asString();
			}
		} catch (Exception ignored) {
		}

		return null;
	}

	/**
	 * POST to google auth servers to get JWT with email of user.
	 *
	 * @param code of the oauth request
	 * @return the JWT
	 * @throws IOException there is an error connection/reading
	 */
	private String getJWT(String code) throws IOException {
		HttpPost request = new HttpPost("https://oauth2.googleapis.com/token");

		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("code", code));
		urlParameters.add(new BasicNameValuePair("client_id", Environment.GOOGLE_CLIENT_ID));
		urlParameters.add(new BasicNameValuePair("client_secret", Environment.GOOGLE_CLIENT_SECRET));
		urlParameters.add(new BasicNameValuePair("redirect_uri", this.getRedirectUri()));
		urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));

		request.setEntity(new UrlEncodedFormEntity(urlParameters));

		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = httpClient.execute(request);

		return JsonParser.parseString(EntityUtils.toString(response.getEntity()))
				.getAsJsonObject().get("id_token").getAsString();

	}

	@Override
	public Result<?> resolveOAuthLoginRequest(HttpServletRequest request, HttpServletResponse response) {
		return defaultRedirect(response, this.oauth2Request.replace("{token}", this.createToken()));
	}

	@Override
	public String getName() {
		return "google";
	}

}
