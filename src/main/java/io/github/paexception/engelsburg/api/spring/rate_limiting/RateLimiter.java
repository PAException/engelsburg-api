package io.github.paexception.engelsburg.api.spring.rate_limiting;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.local.LocalBucketBuilder;
import io.github.paexception.engelsburg.api.util.Error;
import io.github.paexception.engelsburg.api.util.Result;
import lombok.Getter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RateLimiter {

	private final Map<String, Bucket> limiter;
	@Getter
	private final LocalBucketBuilder limit;

	public RateLimiter(LocalBucketBuilder limit) {
		this.limiter = new HashMap<>();
		this.limit = limit;
	}

	public static void sendFailResponse(HttpServletResponse response, long retryAfter) throws IOException {
		response.addHeader("Retry-After", String.valueOf(retryAfter));
		Result.of(Error.TOO_MANY_REQUESTS).respond(response);
	}

	public boolean acquire(String identifier) {
		return this.advancedAcquire(identifier).isConsumed();
	}

	public ConsumptionProbe advancedAcquire(String identifier) {
		if (this.limiter.containsKey(identifier)) {
			return this.limiter.get(identifier).tryConsumeAndReturnRemaining(1);
		} else {
			Bucket bucket = this.limit.build();
			this.limiter.put(identifier, bucket);

			return bucket.tryConsumeAndReturnRemaining(1);
		}
	}

	public <A> A challenge(String identifier, HttpServletResponse response, Supplier<A> onSuccess) throws IOException {
		ConsumptionProbe consumption = this.advancedAcquire(identifier);
		if (!consumption.isConsumed()) {
			sendFailResponse(response, consumption.getNanosToWaitForRefill() / 1000000);
		}

		return onSuccess.get();
	}

}
