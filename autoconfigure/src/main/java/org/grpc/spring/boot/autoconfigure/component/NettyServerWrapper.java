package org.grpc.spring.boot.autoconfigure.component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.grpc.spring.boot.autoconfigure.GRpcServerProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.Endpoint;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

/**
 * @author tolkv
 * @since 13/03/16
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NettyServerWrapper implements InitializingBean, DisposableBean {
  public static final int HEALTH_SERVER_PORT = 6566;
  private final NioEventLoopGroup workerEventExecutors;
  private final NioEventLoopGroup bossEventExecutors;
  private final GRpcServerProperties gRpcServerProperties;
  private final List<Endpoint<?>> endpoints;

  @Override
  public void destroy() throws Exception {
    workerEventExecutors.shutdownGracefully();
    bossEventExecutors.shutdownGracefully();
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    ServerBootstrap serverBootstrap = new ServerBootstrap()
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.TCP_NODELAY, true)
        .option(ChannelOption.SO_BACKLOG, 100)
        .group(bossEventExecutors, workerEventExecutors)
        .childHandler(new ChannelInitializer<Channel>() {
          @Override
          protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpHealthCheckInboundHandler(endpoints));
          }
        });

    log.info("Configure {}, {}", ChannelOption.TCP_NODELAY, true);
    GRpcServerProperties.GRpcServerInstance healthServer = Optional.ofNullable(gRpcServerProperties.getHealthServer())
        .orElseGet(() -> {
          try {
            return GRpcServerProperties.GRpcServerInstance.builder()
                .address(InetAddress.getLocalHost())
                .port(HEALTH_SERVER_PORT)
                .build();
          } catch (UnknownHostException e) {
            log.error("error during choose host for bind health check", e);
          }
          throw new RuntimeException("Http health check server enable, but not configured");
        });

    ChannelFuture bind = serverBootstrap.bind(healthServer.getAddress(), healthServer.getPort());
    bind.addListener(future -> {
      Throwable cause = future.cause();
      if (cause != null)
        throw new RuntimeException("Error during http health check server configure", cause);
      log.info("Health check init on port {}", HEALTH_SERVER_PORT);
    });
  }
}
