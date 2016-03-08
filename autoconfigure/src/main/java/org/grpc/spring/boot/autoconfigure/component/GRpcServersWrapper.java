package org.grpc.spring.boot.autoconfigure.component;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.NettyServerBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.grpc.spring.boot.autoconfigure.GRpcServerProperties;
import org.grpc.spring.boot.autoconfigure.annotation.GRpcService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.grpc.spring.boot.autoconfigure.GRpcServerProperties.GRpcServerInstance;

/**
 * Run GRpc server for listen interface:port
 *
 * @author tolkv
 * @since 07/03/16
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GRpcServersWrapper implements DisposableBean, InitializingBean {
  private static final String GRPC_CLASS_IDENTIFIER = "Grpc";
  private final ApplicationContext applicationContext;
  @Getter
  private final GRpcServerProperties gRpcServerProperties;
  @Getter
  private List<Server> servers = new ArrayList<>(1 /* for main case :) */);

  @Getter
  @Setter
  private Consumer<Server> serverStartHook;

  @Override
  public void destroy() throws Exception {
    servers.forEach(Server::shutdown);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (serverStartHook == null) {
      serverStartHook = this::startServer;
    }
    List<GRpcServerInstance> serverInstanceConfigurations = gRpcServerProperties.getServers();

    List<ServerBuilder> serversBuilders = new ArrayList<>(serverInstanceConfigurations.size());

    // if grps.server[n] does`t configure, use default configuration
    if (serverInstanceConfigurations.isEmpty()) {
      log.warn("GRpc server instances did`t configure. Using default: { address: 127.0.0.1, port: 6565 }");
      serverInstanceConfigurations = new ArrayList<>(1);
      serverInstanceConfigurations.add(new GRpcServerProperties.GRpcServerInstance(6565, InetAddress.getLocalHost()));
    }

    // Log info about starting configuration
    log.info("Try to start GRpc server on {}", serverInstanceConfigurations.stream()
        .map((GRpcServerInstance instance) -> instance.getAddress().toString() + ":" + instance.getPort())
        .collect(Collectors.joining("|")));

    // Create server builders
    serverInstanceConfigurations.stream()
        .map(gRpcServerInstance -> NettyServerBuilder
            .forAddress(
                new InetSocketAddress(
                    gRpcServerInstance.getAddress(),
                    gRpcServerInstance.getPort())
            )
        )
        .forEach(serversBuilders::add);

    // Bind all services and add to server builders
    Stream.of(applicationContext.getBeanNamesForAnnotation(GRpcService.class))
        .map(beanName -> {
          buildServer(applicationContext.getBean(beanName), serversBuilders);
          return beanName;
        })
        .forEach(beanName -> log.info("Service {} has bound", beanName));

    serversBuilders.forEach(this::buildSaveAndStartServer);

    Thread awaitThread = new Thread(new GRpcServersAwaitRunnable(servers), "grpc-await-thread-0");
    awaitThread.setDaemon(false);
    awaitThread.start();
  }

  private void buildServer(Object expectedGRpcServiceBean, List<ServerBuilder> runningServers) {
    Stream.of(expectedGRpcServiceBean.getClass().getInterfaces())
        .filter(aClass -> aClass.getEnclosingClass() != null && aClass.getName().endsWith(GRPC_CLASS_IDENTIFIER))
        .flatMap(aClass -> Stream.of(ReflectionUtils.getAllDeclaredMethods(aClass)))
        .filter(method -> method.getName().equals("bindService")
            && method.getParameterCount() > 0
            && method.getParameterTypes()[0].isAssignableFrom(expectedGRpcServiceBean.getClass()))
        .findFirst()
        .ifPresent(method -> runningServers
            .forEach(serverBuilder -> bindServiceAndAddToServer(expectedGRpcServiceBean, method, serverBuilder)));
  }

  private void buildSaveAndStartServer(ServerBuilder serverBuilder) {
    Server server = serverBuilder.build();
    servers.add(server);
    getServerStartHook().accept(server);
  }

  @SneakyThrows
  private void startServer(Server server) {
    server.start();
    log.info("Server has been starting {}", server);
  }

  private void bindServiceAndAddToServer(Object expectedGRpcServiceBean, Method method, ServerBuilder serverBuilder) {
    ServerServiceDefinition serviceDefinition = invokeBindMethod(expectedGRpcServiceBean, method);
    serverBuilder.addService(serviceDefinition);
    log.info("Add service {} to {}", serviceDefinition.getName(), serverBuilder);
  }

  @SneakyThrows
  private ServerServiceDefinition invokeBindMethod(Object expectedGrpcServiceBean, Method method) {
    return (ServerServiceDefinition) method.invoke(null, expectedGrpcServiceBean);
  }
}