/*
 * Copyright 2019 Robert Winkler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.resilience4j.rxjava3.ratelimiter.operator;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.rxjava3.AbstractSubscriber;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.internal.subscriptions.EmptySubscription;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static io.github.resilience4j.ratelimiter.RequestNotPermitted.createRequestNotPermitted;
import static java.util.Objects.requireNonNull;

class FlowableRateLimiter<T> extends Flowable<T> {

    private final RateLimiter rateLimiter;
    private final Publisher<T> upstream;

    FlowableRateLimiter(Publisher<T> upstream, RateLimiter rateLimiter) {
        this.rateLimiter = requireNonNull(rateLimiter);
        this.upstream = Objects.requireNonNull(upstream, "source is null");
    }

    @Override
    protected void subscribeActual(Subscriber<? super T> downstream) {
        long waitDuration = rateLimiter.reservePermission();
        if (waitDuration >= 0) {
            if (waitDuration > 0) {
                Completable.timer(waitDuration, TimeUnit.NANOSECONDS)
                    .subscribe(() -> upstream.subscribe(new RateLimiterSubscriber(downstream)));
            } else {
                upstream.subscribe(new RateLimiterSubscriber(downstream));
            }
        } else {
            downstream.onSubscribe(EmptySubscription.INSTANCE);
            downstream.onError(createRequestNotPermitted(rateLimiter));
        }
    }

    class RateLimiterSubscriber extends AbstractSubscriber<T> {

        RateLimiterSubscriber(Subscriber<? super T> downstreamSubscriber) {
            super(downstreamSubscriber);
        }

        @Override
        protected void hookOnError(Throwable e) {
            rateLimiter.onError(e);
        }

        @Override
        protected void hookOnNext(T value) {
            rateLimiter.onResult(value);
        }

        @Override
        public void hookOnComplete() {
            // NoOp
        }

        @Override
        public void hookOnCancel() {
            // NoOp
        }
    }

}