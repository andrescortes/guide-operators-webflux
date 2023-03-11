package com.reactivespring.util;

import com.reactivespring.exception.MoviesInfoServerException;
import java.time.Duration;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

public class RetryUtil {

    private RetryUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Retry retrySpec() {
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
            .filter(MoviesInfoServerException.class::isInstance)
            .onRetryExhaustedThrow(
                (retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));
    }
}
