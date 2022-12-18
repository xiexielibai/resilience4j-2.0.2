/*
 *
 *  Copyright 2021: Matthew Sandoz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package io.github.resilience4j.hedge.event;

import java.time.Duration;

public class HedgeOnPrimaryFailureEvent extends AbstractHedgeEvent {

    private final Throwable throwable;

    public HedgeOnPrimaryFailureEvent(String hedgeName, Duration duration, Throwable throwable) {
        super(hedgeName, Type.PRIMARY_FAILURE, duration);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return String.format("%s: Hedge '%s' recorded an error: '%s' in %dms",
            getCreationTime(),
            getHedgeName(),
            getThrowable(),
            getDuration().toMillis());
    }

}
