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
package io.github.resilience4j.springboot3.circuitbreaker.autoconfigure;

import io.github.resilience4j.spring6.circuitbreaker.configure.CircuitBreakerConfigurationProperties;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.consumer.EventConsumerRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfigurationOnMissingBean extends
    AbstractCircuitBreakerConfigurationOnMissingBean {

    public CircuitBreakerConfigurationOnMissingBean(
        CircuitBreakerConfigurationProperties circuitBreakerProperties) {
        super(circuitBreakerProperties);
    }

    @Bean
    @ConditionalOnMissingBean(value = CircuitBreakerEvent.class, parameterizedContainer = EventConsumerRegistry.class)
    public EventConsumerRegistry<CircuitBreakerEvent> eventConsumerRegistry() {
        return circuitBreakerConfiguration.eventConsumerRegistry();
    }

}
