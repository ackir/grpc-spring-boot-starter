package org.grpc.spring.boot.autoconfigure.component;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.netty.NettyServerBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.grpc.spring.boot.autoconfigure.GRpcServerProperties;
import org.grpc.spring.boot.autoconfigure.annotation.GRpcService;
import org.grpc.spring.boot.autoconfigure.context.NettyServerStartingEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.ReflectionUtils;

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
  private static final String                    GRPC_CLASS_IDENTIFIER = "Grpc";
  private final        ApplicationContext        applicationContext;
  @Getter
  private final        GRpcServerProperties      gRpcServerProperties;
  private final        ApplicationEventPublisher publisher;
  @Getter
  private              List<Server>              servers               = new ArrayList<>(1 /* for main case :) */);

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
      serverInstanceConfigurations.add(new GRpcServerProperties.GRpcServerInstance(6565, InetAddress.getLocalHost(), null));
    }

    List<GRpcServerInstance> serverInstanceConfigurationsFiltered = serverInstanceConfigurations.stream()
        .filter(this::filterGRpcServerInstanceConfiguratino).collect(Collectors.toList());

    // Log info about starting configuration
    log.info("Try to start GRpc server on {}", serverInstanceConfigurationsFiltered.stream()
        .map((GRpcServerInstance instance) -> extractAddress(instance) + ":" + instance.getPort())
        .collect(Collectors.joining("|")));

    // Create server builders
    serverInstanceConfigurationsFiltered.stream()
        .map(gRpcServerInstance -> {
              if (gRpcServerInstance.getProcessName() != null) {
                return InProcessServerBuilder.forName(gRpcServerInstance.getProcessName());
              }

              return NettyServerBuilder
                  .forAddress(
                      new InetSocketAddress(
                          gRpcServerInstance.getAddress(),
                          gRpcServerInstance.getPort())
                  );
            }
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

  private String extractAddress(GRpcServerInstance instance) {
    if(instance != null && instance.getAddress() != null) {
      return instance.getAddress().toString();
    }
    return "<empty>";
  }

  private boolean filterGRpcServerInstanceConfiguratino(GRpcServerInstance gRpcServerInstance) {
    boolean isValid = (gRpcServerInstance.getAddress() != null && gRpcServerInstance.getPort() != null)
        || gRpcServerInstance.getProcessName() != null;

    if (!isValid) {
      log.info("server {} is not valid. Skipped", gRpcServerInstance);
    }
    return isValid;
  }

  private void buildServer(Object expectedGRpcServiceBean, List<ServerBuilder> runningServers) {
    Stream.of(expectedGRpcServiceBean.getClass().getSuperclass())
        .filter(aClass -> aClass.getEnclosingClass() != null && aClass.getEnclosingClass().getName().endsWith(GRPC_CLASS_IDENTIFIER))
        .flatMap(aClass -> Stream.of(ReflectionUtils.getAllDeclaredMethods(aClass.getEnclosingClass())))
        .filter(method -> method.getName().equals("newStub")
            && method.getParameterCount() > 0
            && method.getParameterTypes()[0].isAssignableFrom(io.grpc.Channel.class))
        .findFirst()
        .ifPresent(method -> runningServers
            .forEach(serverBuilder -> addServiceToServer(expectedGRpcServiceBean, serverBuilder)));
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
    publisher.publishEvent(new NettyServerStartingEvent(applicationContext, server));
  }

  private void addServiceToServer(Object expectedGRpcServiceBean, ServerBuilder serverBuilder) {
//    ServerServiceDefinition serviceDefinition = invokeBindMethod(expectedGRpcServiceBean, method);
    BindableService asBindableService = (BindableService) expectedGRpcServiceBean;
    serverBuilder.addService(asBindableService);
    log.info("Add service {} to {}", expectedGRpcServiceBean, serverBuilder);
  }
}