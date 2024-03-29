/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.spring.rate_limiting;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

/**
 * Interceptor to handle general rate limits.
 *
 * <p>Function to exclude must be annotated as {@link IgnoreGeneralRateLimit}.</p>
 */
@Component
public class RateLimitInterceptor extends RateLimiter implements HandlerInterceptor {

	/**
	 * Initialize rate limiting buckets.
	 */
	public RateLimitInterceptor() {
		super(Bucket.builder()
				.addLimit(Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1))))
				.addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofSeconds(10))))
		);
	}

	/**
	 * Check if remote address is out of requests.
	 * Send fail response if true otherwise pass.
	 *
	 * <p>Checks also for controller implementations of {@link RateLimiter}.
	 * Acquires also bucket resources like the general implementation.</p>
	 *
	 * @param request  given by spring with remote address
	 * @param response to eventually write failure
	 * @param handler  to get information about the method
	 * @return if request should proceed -> false of buckets are out of requests, true otherwise
	 * @throws Exception if something goes wrong while writing the failure response
	 */
	@Override
	public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull Object handler) throws Exception {
		if (!(handler instanceof HandlerMethod)) return true;

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		if (handlerMethod.hasMethodAnnotation(IgnoreGeneralRateLimit.class)) return true;

		ConsumptionProbe consumption = this.advancedAcquire(request.getRemoteAddr());
		if (consumption != null && !consumption.isConsumed()) {
			sendFailResponse(response, consumption.getNanosToWaitForRefill() / 1000000000);
			return false;
		}

		if (handlerMethod.getBean() instanceof RateLimiter && handlerMethod.hasMethodAnnotation(RateLimit.class)) {
			RateLimiter rateLimiter = (RateLimiter) handlerMethod.getBean();
			consumption = rateLimiter.advancedAcquire(request.getRemoteAddr());
			if (consumption != null && !consumption.isConsumed()) {
				sendFailResponse(response, consumption.getNanosToWaitForRefill() / 1000000000);
				return false;
			}
		}

		return true;
	}
}
