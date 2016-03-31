package org.grpc.spring.boot.autoconfigure;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.nio.NioEventLoopGroup;
import org.grpc.spring.boot.autoconfigure.component.NettyServerWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author tolkv
 * @since 07/03/16
 */
@Configuration
@ConditionalOnClass({EndpointAutoConfiguration.class})
@AutoConfigureAfter({EndpointAutoConfiguration.class})
@ConditionalOnProperty(name = "grpc.health.http.enabled", matchIfMissing = true)
@EnableConfigurationProperties(GRpcServerProperties.class)
public class NettyHttpHealthCheckAutoConfiguration {

  @ConditionalOnMissingBean(type = {"grpc.health.boss.thread-pool"})
  @Bean(name = "grpc.health.boss.thread-pool")
  NioEventLoopGroup bossExecutorService() {
    return new NioEventLoopGroup(1,
        new ThreadFactoryBuilder().setNameFormat("grpc-boss-health-%d").build()
    );
  }

  @ConditionalOnMissingBean(type = {"grpc.health.worker.thread-pool"})
  @Bean(name = "grpc.health.worker.thread-pool")
  NioEventLoopGroup workerExecutorService() {
    return new NioEventLoopGroup(70,
        new ThreadFactoryBuilder().setNameFormat("grpc-worker-health-%d").build()
    );
  }

  @Bean
  @Autowired
  NettyServerWrapper nettyServerWrapper(GRpcServerProperties grpcProperties,
                                        List<Endpoint<?>> endpoints,
                                        @Qualifier("grpc.health.worker.thread-pool") NioEventLoopGroup workerLoop,
                                        @Qualifier("grpc.health.boss.thread-pool") NioEventLoopGroup bossLoop) {
    return new NettyServerWrapper(workerLoop, bossLoop, grpcProperties, endpoints);
  }
}
