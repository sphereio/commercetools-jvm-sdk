package io.sphere.sdk.retry;

import javax.annotation.Nullable;
import java.time.Duration;

public final class RetryBehaviour<P> {
    enum Strategy {
        RESUME, STOP, RETRY_IMMEDIATELY, RETRY_SCHEDULED;
    }

    private final Strategy strategy;
    @Nullable
    private final Throwable error;
    @Nullable
    private final P parameter;
    @Nullable
    private final Duration duration;

    private RetryBehaviour(final Strategy strategy, final Throwable error, final P parameter, final Duration duration) {
        this.strategy = strategy;
        this.duration = duration;
        this.error = error;
        this.parameter = parameter;
    }

    public static <P> RetryBehaviour<P> resume(final Throwable error) {
        return new RetryBehaviour<>(Strategy.RESUME, error, null, null);
    }

    public static <P> RetryBehaviour<P> stop(final Throwable error) {
        return new RetryBehaviour<>(Strategy.STOP, error, null, null);
    }

    public static <P> RetryBehaviour<P> retryImmediately(final P parameter) {
        return new RetryBehaviour<>(Strategy.RETRY_IMMEDIATELY, null, parameter, null);
    }

    public static <P> RetryBehaviour<P> retryScheduled(final P parameter, final Duration duration) {
        return new RetryBehaviour<>(Strategy.RETRY_SCHEDULED, null, parameter, duration);
    }

    @Nullable
    Duration getDuration() {
        return duration;
    }

    @Nullable
    Throwable getError() {
        return error;
    }

    @Nullable
    P getParameter() {
        return parameter;
    }

    Strategy getStrategy() {
        return strategy;
    }
}
