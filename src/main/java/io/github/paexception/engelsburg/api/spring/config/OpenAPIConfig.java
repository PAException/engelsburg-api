/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.spring.config;

import com.google.common.reflect.ClassPath;
import io.github.paexception.engelsburg.api.endpoint.dto.UserDTO;
import io.github.paexception.engelsburg.api.endpoint.dto.response.SubstituteNotificationDTO;
import io.github.paexception.engelsburg.api.spring.auth.AuthScope;
import io.github.paexception.engelsburg.api.spring.auth.Authorization;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponse;
import io.github.paexception.engelsburg.api.util.openapi.ErrorResponses;
import io.github.paexception.engelsburg.api.util.openapi.Response;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles every setting related to open api.
 */
@Configuration
public class OpenAPIConfig {

	private final static List<String> EXCLUDE_LOAD = List.of(
			UserDTO.class.getSimpleName(),
			SubstituteNotificationDTO.class.getSimpleName()
	);
	private final Map<String, Schema<?>> schemas = new HashMap<>();

	/**
	 * Customize default settings of the open api.
	 *
	 * @return customized openApi
	 */
	@Bean
	public OpenApiCustomiser openApiCustomizer() {
		return openApi -> {
			this.addSchemas();
			this.schemas.forEach((key, schema) -> openApi.getComponents().addSchemas(key, schema));

			openApi
					.servers(List.of(
							new Server()
									.url("https://engelsburg-api.com")
									.description("Production server"),
							new Server()
									.url("http://localhost:8080")
									.description("Development server")))
					.info(new Info()
							.title("Engelsburg-API")
							.description("Unofficial API of the Engelsburg-Gymnasium-Kassel")
							.version("v1"));
		};
	}

	/**
	 * Load all schemas of classes which name contains 'DTO'.
	 */
	@SuppressWarnings("UnstableApiUsage")
	public void addSchemas() {
		try {
			ClassPath.from(ClassLoader.getSystemClassLoader())
					.getAllClasses()
					.stream()
					.filter(clazz -> clazz.getSimpleName().endsWith("DTO")
							&& !EXCLUDE_LOAD.contains(clazz.getSimpleName())
					).map(ClassPath.ClassInfo::load)
					.forEach(clazz -> {
						ResolvedSchema schema = ModelConverters.getInstance().readAllAsResolvedSchema(clazz);

						if (schema.schema != null) this.schemas.put(clazz.getSimpleName(), schema.schema);
						schema.referencedSchemas.forEach(this.schemas::put);
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Modifies all operations of the default endpoint documentation.
	 * Among them are paging, response annotations, error response annotations, authorization and scopes,
	 * default error responses of authorization and a summary of the operation by the method name.
	 *
	 * @return modified operations
	 * @see Response
	 * @see ErrorResponse
	 * @see AuthScope
	 */
	@Bean
	public OperationCustomizer customOperations() {
		return (Operation operation, HandlerMethod handlerMethod) -> {
			//Initialise variables for null safety
			ApiResponses responses = new ApiResponses();
			if (operation.getResponses() != null) responses = operation.getResponses();
			final List<Parameter> parameters = new ArrayList<>();
			if (operation.getParameters() != null) parameters.addAll(operation.getParameters());

			//Modify paging parameter
			if (parameters.stream().anyMatch(parameter -> parameter.getName().equals("paging"))) {

				Parameter pageParam = new Parameter()
						.in(ParameterIn.QUERY.toString())
						.schema(new IntegerSchema())
						.name("page")
						.example(0)
						.required(false);

				Parameter sizeParam = new Parameter()
						.in(ParameterIn.QUERY.toString())
						.schema(new IntegerSchema())
						.name("size")
						.example(20)
						.required(false);

				parameters.removeIf(parameter -> parameter.getName().equals("paging"));
				parameters.add(pageParam);
				parameters.add(sizeParam);
			}

			//Replace all object as request param in query which contains param in name with any param
			parameters.stream()
					.filter(parameter -> parameter.getIn().equals("query")
							&& StringUtils.containsIgnoreCase(parameter.getName(), "param"))
					.findAny()
					.ifPresent(parameter -> {
						parameters.remove(parameter);
						parameters.add(
								new Parameter()
										.in("query")
										.name("[any]")
										.schema(new StringSchema().example(parameter.getSchema().getExample()))
										.required(true)
										.description(parameter.getSchema().getDescription())
						);
					});

			//Set response schema by @Response method annotation
			Response response = handlerMethod.getMethodAnnotation(Response.class);
			if (response != null) {
				//If schemas are not loaded, load
				if (this.schemas.isEmpty()) this.addSchemas();
				//If void set schema to null otherwise to specified schema
				Schema<?> schema = !response.value().equals(Void.class)
						? this.schemas.get(response.value().getSimpleName())
						: null;
				//Object schema
				if (response.value().equals(Object.class)) schema = new Schema<>().type("object");

				responses.addApiResponse(
						"200",
						new ApiResponse()
								.content(
										new Content().addMediaType(
												"application/json",
												//If schema is null set empty media type, otherwise with schema
												schema != null
														? new MediaType().schema(schema)
														: new MediaType()))
								.description(response.description()));
			}

			//Get all annotated @ErrorResponse's of a method
			ErrorResponse[] errors = {};
			ErrorResponses errorResponses = handlerMethod.getMethodAnnotation(ErrorResponses.class);
			if (errorResponses != null) errors = errorResponses.value();
			ErrorResponse errorResponse = handlerMethod.getMethodAnnotation(ErrorResponse.class);
			if (errorResponse != null) {
				errors = Arrays.copyOf(errors, errors.length + 1);
				errors[errors.length - 1] = errorResponse;
			}
			//If any annotated present set exact error schemas
			if (errors.length > 0) {
				for (ErrorResponse error : errors) {

					String status = String.valueOf(error.status());
					String messageKey = error.messageKey();
					String extra = error.extra().equals("") ? null : error.extra();
					String key = error.key().equals("") ? status : status + " - " + error.key();
					String description = error.description();

					//Add error response
					responses.addApiResponse(
							key,
							new ApiResponse()
									.content(this.errorSchema(status, messageKey, extra))
									.description(description)
					);
				}
			}

			//Get all @AuthScope annotations of method
			Set<String> scopes = new HashSet<>();
			AuthScope[] authScopes = {};
			Authorization authorization = handlerMethod.getMethodAnnotation(Authorization.class);
			if (authorization != null) authScopes = authorization.value();
			AuthScope authScope = handlerMethod.getMethodAnnotation(AuthScope.class);
			if (authScope != null) {
				authScopes = Arrays.copyOf(authScopes, authScopes.length + 1);
				authScopes[authScopes.length - 1] = authScope;
			}
			//If present set default possible error responses
			if (authScopes.length > 0) {
				//Add scopes to set if present
				for (AuthScope scope : authScopes) {
					if (!scope.scope().equals("")) scopes.add(scope.scope());
					if (!scope.value().equals("")) scopes.add(scope.value());
				}

				responses.addApiResponse(
						"401 - Unauthorized",
						new ApiResponse()
								.content(this.errorSchema("400", "UNAUTHORIZED", "token"))
								.description("If no accessToken was provided")
				);
				responses.addApiResponse(
						"400 - Expired",
						new ApiResponse()
								.content(this.errorSchema("400", "EXPIRED", "token"))
								.description("If the accessToken is expired")
				);
				responses.addApiResponse(
						"400 - Invalid",
						new ApiResponse()
								.content(this.errorSchema("400", "INVALID", "token"))
								.description("If the token has an invalid format")
				);
				responses.addApiResponse(
						"400 - Failed",
						new ApiResponse()
								.content(this.errorSchema("400", "FAILED", "token"))
								.description("If the token verification failed")
				);

				if (!scopes.isEmpty()) responses.addApiResponse(
						"403 - Forbidden",
						new ApiResponse()
								.content(this.errorSchema("403", "FORBIDDEN", "auth"))
								.description("If the user of sent token is not has not the required scopes")
				);
			}

			//If method parameter has userDTO then set authentication parameter
			if (parameters.stream().anyMatch(parameter -> parameter.getName().equals("userDTO")) || !scopes.isEmpty()) {
				boolean required = handlerMethod.hasMethodAnnotation(AuthScope.class)
						|| handlerMethod.hasMethodAnnotation(Authorization.class);

				//Build required scopes text
				String requiredScopes = "";
				if (!scopes.isEmpty()) {
					StringBuilder builder = new StringBuilder(". Required scopes: [");

					scopes.forEach(scope -> builder.append(scope).append(", "));
					builder.delete(builder.length() - 2, builder.length());

					requiredScopes = builder.append("]").toString();
				}

				//Set authentication parameter
				Parameter authParam = new Parameter()
						.in(ParameterIn.HEADER.toString())
						.schema(new StringSchema().example(
								"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiMDE4NTQyNS04MDRjLTRhYTYtYWE2OC00NDhjZjQ1MDAwZmUiLCJpc3MiOiJlbmdlbHNidXJnLWFwaSIsInNjb3BlcyI6ImluZm8uY2xhc3Nlcy5yZWFkLmFsbC0tdGVhY2hlci5yZWFkLmFsbC0tLW5vdGlmaWNhdGlvbi5zZXR0aW5ncy5yZWFkLnNlbGYtd3JpdGUuc2VsZi0tLXN1YnN0aXR1dGUubWVzc2FnZS5yZWFkLmN1cnJlbnQtLXJlYWQuY3VycmVudC0tdXNlci5kYXRhLmRlbGV0ZS5zZWxmLXJlYWQuc2VsZiIsImV4cCI6MTY0NTQ2NjM0MiwiaWF0IjoxNjQ1NDY2MDQyfQ.qSayTfpQtIi94qEP9Ud8dZManoWI-LGmHa2eFyY2-apyklbTqOU3LyJ4sVHdK3svxgWwvoE83ptGYPnI7txrvw"))
						.name("Authorization")
						.description("AccessToken to authenticate user" + requiredScopes)
						.required(required);

				//Remove userDTO from parameters, add authentication param
				parameters.removeIf(parameter -> parameter.getName().equals("userDTO"));
				parameters.add(authParam);
			}

			//If method parameter has semester then set semester parameter
			if (parameters.stream().anyMatch(parameter -> parameter.getName().equals("semester"))) {

				//Set semester parameter
				Parameter semesterParam = new Parameter()
						.in(ParameterIn.QUERY.toString())
						.schema(new IntegerSchema().example("43"))
						.name("semester")
						.description(
								"Specify the semester to use. Only required if the user has not a current semester set.")
						.required(false);

				//Remove semester from parameters, add semester param
				parameters.removeIf(parameter -> parameter.getName().equals("semester"));
				parameters.add(semesterParam);

				//Add error responses
				responses.addApiResponse(
						"403 - Forbidden - Semester",
						new ApiResponse()
								.content(this.errorSchema("403", "FORBIDDEN", "semester"))
								.description(
										"If tries to read a semester that is not his and does not have the permission to do so")
				);
				responses.addApiResponse(
						"404 - Not found - Semester",
						new ApiResponse()
								.content(this.errorSchema("404", "NOT_FOUND", "semester"))
								.description("If specified the semester was not found")
				);
			}

			//Set summary of operation by method name LowerCamelCase to 'FirstUpperWithSpaceCase'
			StringBuilder builder = new StringBuilder();
			String summary = handlerMethod.getMethod().getName();
			for (int i = 0; i < summary.length(); i++) {
				char c = summary.charAt(i);

				if (i == 0) builder.append(Character.toUpperCase(c));
				else if (Character.isUpperCase(c)) builder.append(" ").append(Character.toLowerCase(c));
				else builder.append(c);
			}
			operation.setSummary(builder.toString());

			return operation.responses(responses).parameters(parameters);
		};
	}

	/**
	 * Build a basic error schema with specific examples.
	 *
	 * @param status     for example
	 * @param messageKey for example
	 * @param extra      for example
	 * @return error schema
	 */
	private Content errorSchema(String status, String messageKey, String extra) {
		return new Content().addMediaType(
				"application/json",
				new MediaType().schema(
						new Schema<>().type("object").addProperties(
								"status",
								new IntegerSchema().example(status)
						).addProperties(
								"messageKey",
								new StringSchema().example(messageKey)
						).addProperties(
								"extra",
								new StringSchema().example(extra)
						)
				)
		);
	}
}
