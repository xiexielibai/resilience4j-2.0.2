package io.github.resilience4j.common;

import java.util.function.Predicate;

public class RecordResultPredicate implements Predicate<Object> {
    @Override
    public boolean test(Object o) {
        return false;
    }
}
