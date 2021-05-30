package io.github.paexception.engelsburg.api.endpoint;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.paexception.engelsburg.api.authorization.interceptor.AuthScope;
import io.github.paexception.engelsburg.api.controller.userdata.UserDataController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class UserDataEndpoint {

	@Autowired
	private UserDataController userController;

	@AuthScope("user.data.read.self")
	@GetMapping("/user/data/get")
	public Object getData(DecodedJWT jwt) {
		return this.userController.getUserData(jwt).getHttpResponse();
	}

	@AuthScope("user.data.delete.self")
	@DeleteMapping("/user/data/delete")
	public Object deleteData(DecodedJWT jwt) {
		return this.userController.deleteUserData(jwt).getHttpResponse();
	}

}
