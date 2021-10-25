package io.github.paexception.engelsburg.api.spring.rate_limiting;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

/**
 * Interceptor to handle general rate limits.
 */
public class RateLimitInterceptor extends RateLimiter implements HandlerInterceptor {

	public RateLimitInterceptor() {
		super(Bucket4j.builder()
				.addLimit(Bandwidth.classic(30, Refill.intervally(30, Duration.ofMinutes(1))))
				.addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofSeconds(15))))
		);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		if (handlerMethod.hasMethodAnnotation(IgnoreRateLimit.class)) return true;

		ConsumptionProbe consumption = this.advancedAcquire(request.getRemoteAddr());
		if (!consumption.isConsumed()) {
			sendFailResponse(response, consumption.getNanosToWaitForRefill() / 1000000000);
			return false;
		}

		if (handlerMethod.getBean() instanceof RateLimiter && handlerMethod.hasMethodAnnotation(RateLimit.class)) {
			RateLimiter rateLimiter = (RateLimiter) handlerMethod.getBean();
			consumption = rateLimiter.advancedAcquire(request.getRemoteAddr());
			if (!consumption.isConsumed()) {
				sendFailResponse(response, consumption.getNanosToWaitForRefill() / 1000000000);
				return false;
			}
		}

		return true;
	}
}
