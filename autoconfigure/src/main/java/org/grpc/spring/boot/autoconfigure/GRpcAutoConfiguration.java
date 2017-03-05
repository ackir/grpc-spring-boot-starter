package org.grpc.spring.boot.autoconfigure;

import org.grpc.spring.boot.autoconfigure.annotation.GRpcService;
import org.grpc.spring.boot.autoconfigure.component.GRpcServersWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tolkv
 * @since 07/03/16
 */
@Configuration
@ConditionalOnClass(GRpcService.class)
@ConditionalOnProperty(name = "grpc.enabled", matchIfMissing = true)
@EnableConfigurationProperties(GRpcServerProperties.class)
public class GRpcAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean(GRpcServersWrapper.class)
  public GRpcServersWrapper gRpcServersWrapper(ApplicationContext applicationContext,
                                               GRpcServerProperties gRpcServerProperties,
                                               ApplicationEventPublisher applicationEventPublisher) {
    return new GRpcServersWrapper(applicationContext, gRpcServerProperties, applicationEventPublisher);
  }
}
