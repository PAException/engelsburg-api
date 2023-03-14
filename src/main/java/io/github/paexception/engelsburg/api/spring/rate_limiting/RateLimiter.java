/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.spring.rate_limiting;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.local.LocalBucketBuilder;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.Getter;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to identify extending class as rate limiting.
 *
 * <p>Can be used as field instead of extending a class but will lose
 * the ability to be auto checked by {@link RateLimitInterceptor}.
 * Useful for custom implementations and logic.</p>
 *
 * <p>Functions of extending class to rate limit must be annotated as {@link RateLimit}.</p>
 */
public class RateLimiter {

	private final Map<String, Bucket> limiter;
	@Getter
	private final LocalBucketBuilder limit;

	public RateLimiter(LocalBucketBuilder limit) {
		this.limiter = new HashMap<>();
		this.limit = limit;
	}

	/**
	 * Send a TOO_MANY_REQUESTS fail response with Retry-After header field.
	 *
	 * @param response   to write response
	 * @param retryAfter in seconds
	 * @throws IOException if something goes wrong while writing the response
	 */
	public static void sendFailResponse(HttpServletResponse response, long retryAfter) throws IOException {
		response.addHeader("Retry-After", String.valueOf(retryAfter));
		Result.of(Error.TOO_MANY_REQUESTS).respond(response);
	}

	/**
	 * Simple acquire of a request of the bucket.
	 *
	 * @param identifier usually a remote address
	 * @return true if request can be acquired
	 */
	public boolean acquire(String identifier) {
		ConsumptionProbe probe = this.advancedAcquire(identifier);
		return probe != null && probe.isConsumed();
	}

	/**
	 * Acquire by identifier.
	 *
	 * @param identifier usually a remote address.
	 * @return consumption probe to customise response
	 * like time to refill and remaining requests
	 */
	@Nullable
	public ConsumptionProbe advancedAcquire(String identifier) {
		if (identifier.equals("127.0.0.1") || identifier.equals("localhost")) return null;
		if (this.limiter.containsKey(identifier)) {
			return this.limiter.get(identifier).tryConsumeAndReturnRemaining(1);
		} else {
			Bucket bucket = this.limit.build();
			this.limiter.put(identifier, bucket);

			return bucket.tryConsumeAndReturnRemaining(1);
		}
	}
}
