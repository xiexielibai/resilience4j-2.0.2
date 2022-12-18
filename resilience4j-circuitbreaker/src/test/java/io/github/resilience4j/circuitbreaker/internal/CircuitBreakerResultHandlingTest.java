package io.github.resilience4j.circuitbreaker.internal;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.control.Either;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom;
import static org.assertj.core.api.Assertions.assertThat;

public class CircuitBreakerResultHandlingTest {

    @Test
    public void shouldRecordSpecificStringResultAsAFailureAndAnyOtherAsSuccess() {
        CircuitBreaker circuitBreaker = new CircuitBreakerStateMachine("testName", custom()
            .slidingWindowSize(5)
            .recordResult(result -> result.equals("failure"))
            .build());

        assertThat(circuitBreaker.tryAcquirePermission()).isTrue();
        circuitBreaker.onResult(0, TimeUnit.NANOSECONDS, "success");

        // Call 2 is a failure
        assertThat(circuitBreaker.tryAcquirePermission()).isTrue();
        circuitBreaker.onResult(0, TimeUnit.NANOSECONDS, "failure");

        assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(1);
        assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls()).isEqualTo(1);
        assertThat(circuitBreaker.getMetrics().getNumberOfBufferedCalls()).isEqualTo(2);
    }

    @Test
    public void shouldRecordSpecificComplexResultAsAFailureAndAnyOtherAsSuccess() {
        CircuitBreaker circuitBreaker = new CircuitBreakerStateMachine("testName", custom()
            .slidingWindowSize(5)
            .recordResult(result ->
                result instanceof Either && ((Either) result).isLeft() && ((Either) result).getLeft().equals("failure")
            )
            .build());

        assertThat(circuitBreaker.tryAcquirePermission()).isTrue();
        circuitBreaker.onResult(0, TimeUnit.NANOSECONDS, Either.left("accepted fail"));

        // Call 2 is a failure
        assertThat(circuitBreaker.tryAcquirePermission()).isTrue();
        circuitBreaker.onResult(0, TimeUnit.NANOSECONDS, Either.left("failure"));

        assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(1);
        assertThat(circuitBreaker.getMetrics().getNumberOfSuccessfulCalls()).isEqualTo(1);
        assertThat(circuitBreaker.getMetrics().getNumberOfBufferedCalls()).isEqualTo(2);
    }
}
