package io.github.resilience4j.rxjava3;


import io.reactivex.rxjava3.core.MaybeObserver;

import static java.util.Objects.requireNonNull;

public abstract class AbstractMaybeObserver<T> extends AbstractDisposable implements
    MaybeObserver<T> {

    private final MaybeObserver<? super T> downstreamObserver;

    public AbstractMaybeObserver(MaybeObserver<? super T> downstreamObserver) {
        this.downstreamObserver = requireNonNull(downstreamObserver);
    }

    @Override
    protected void hookOnSubscribe() {
        downstreamObserver.onSubscribe(this);
    }

    @Override
    public void onError(Throwable e) {
        whenNotCompleted(() -> {
            hookOnError(e);
            downstreamObserver.onError(e);
        });
    }

    @Override
    public void onComplete() {
        whenNotCompleted(() -> {
            hookOnComplete();
            downstreamObserver.onComplete();
        });
    }

    protected abstract void hookOnComplete();

    protected abstract void hookOnError(Throwable e);

    @Override
    public void onSuccess(T value) {
        whenNotCompleted(() -> {
            hookOnSuccess(value);
            downstreamObserver.onSuccess(value);
        });
    }

    protected abstract void hookOnSuccess(T value);

}
