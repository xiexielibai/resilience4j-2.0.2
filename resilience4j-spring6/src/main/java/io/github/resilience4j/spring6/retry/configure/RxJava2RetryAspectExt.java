/*
 * Copyright 2019 Mahmoud Romeh
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
package io.github.resilience4j.spring6.retry.configure;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.transformer.RetryTransformer;
import io.github.resilience4j.spring6.utils.AspectUtil;
import io.reactivex.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * the Rx Retry logic support for the spring AOP conditional on the presence of Rx classes on the
 * spring class loader
 */
public class RxJava2RetryAspectExt implements RetryAspectExt {

    private static final Logger logger = LoggerFactory.getLogger(RxJava2RetryAspectExt.class);
    private final Set<Class> rxSupportedTypes = AspectUtil.newHashSet(ObservableSource.class,
        SingleSource.class, CompletableSource.class, MaybeSource.class, Flowable.class);

    /**
     * @param returnType the AOP method return type class
     * @return boolean if the method has Rx java 2 rerun type
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean canHandleReturnType(Class returnType) {
        return rxSupportedTypes.stream()
            .anyMatch(classType -> classType.isAssignableFrom(returnType));
    }

    /**
     * @param proceedingJoinPoint Spring AOP proceedingJoinPoint
     * @param retry               the configured Retry
     * @param methodName          the method name
     * @return the result object
     * @throws Throwable exception in case of faulty flow
     */
    @Override
    public Object handle(ProceedingJoinPoint proceedingJoinPoint, Retry retry, String methodName)
        throws Throwable {
        RetryTransformer<?> retryTransformer = RetryTransformer.of(retry);
        Object returnValue = proceedingJoinPoint.proceed();
        return executeRxJava2Aspect(retryTransformer, returnValue);
    }

    @SuppressWarnings("unchecked")
    private Object executeRxJava2Aspect(RetryTransformer retryTransformer, Object returnValue) {
        if (returnValue instanceof ObservableSource) {
            Observable<?> observable = (Observable<?>) returnValue;
            return observable.compose(retryTransformer);
        } else if (returnValue instanceof SingleSource) {
            Single<?> single = (Single) returnValue;
            return single.compose(retryTransformer);
        } else if (returnValue instanceof CompletableSource) {
            Completable completable = (Completable) returnValue;
            return completable.compose(retryTransformer);
        } else if (returnValue instanceof MaybeSource) {
            Maybe<?> maybe = (Maybe) returnValue;
            return maybe.compose(retryTransformer);
        } else if (returnValue instanceof Flowable) {
            Flowable<?> flowable = (Flowable) returnValue;
            return flowable.compose(retryTransformer);
        } else {
            logger.error("Unsupported type for retry RxJava2 {}",
                returnValue.getClass().getTypeName());
            throw new IllegalArgumentException(
                "Not Supported type for the Retry in RxJava2 :" + returnValue.getClass().getName());
        }
    }
}
