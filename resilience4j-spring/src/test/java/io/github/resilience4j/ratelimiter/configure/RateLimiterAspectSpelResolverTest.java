/*
 * Copyright 2020 Kyuhyen Hwang
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
package io.github.resilience4j.ratelimiter.configure;

import io.github.resilience4j.TestApplication;
import io.github.resilience4j.TestDummyService;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class RateLimiterAspectSpelResolverTest {
    @Autowired
    @Qualifier("rateLimiterDummyService")
    TestDummyService testDummyService;

    @Autowired
    RateLimiterRegistry registry;

    @Test
    public void testSpel() {
        assertThat(registry.getAllRateLimiters().stream().filter(it -> it.getName().equals("SPEL_BACKEND")).findAny().isPresent()).isFalse();
        assertThat(testDummyService.spelSync("SPEL_BACKEND")).isEqualTo("recovered");
        assertThat(registry.getAllRateLimiters().stream().filter(it -> it.getName().equals("SPEL_BACKEND")).findAny().isPresent()).isTrue();
    }
}
