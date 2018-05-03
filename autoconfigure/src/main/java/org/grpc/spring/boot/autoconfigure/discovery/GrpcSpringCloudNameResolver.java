package org.grpc.spring.boot.autoconfigure.discovery;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GrpcSpringCloudNameResolver extends NameResolver {
  private final URI             targetUri;
  private final Attributes      params;
  private final DiscoveryClient discoveryClient;

  private Listener listener;

  @Override
  public final synchronized void refresh() {
    Preconditions.checkState(listener != null, "not started");
    resolveAllServices();
  }

  @Override
  public String getServiceAuthority() {
    return targetUri.toASCIIString();
  }

  @Override
  public void start(Listener listener) {
    this.listener = Preconditions.checkNotNull(listener, "listener");
    resolveAllServices();
  }

  private void resolveAllServices() {
    try {
      List<ServiceInstance> instances = discoveryClient.getInstances(targetUri.toString().replace("spring.", ""));

      List<SocketAddress> instanceAddresses = instances.stream()
          .map(serviceInstance -> new InetSocketAddress(serviceInstance.getHost(), serviceInstance.getPort()))
          .collect(Collectors.toList());

      EquivalentAddressGroup equivalentAddressGroup = new EquivalentAddressGroup(instanceAddresses);

      listener.onAddresses(Collections.singletonList(equivalentAddressGroup), params);
    } catch (Exception e) {
      log.error("[grpc] error while resolving services", e);
    }
  }

  @Override
  public void shutdown() {
    log.info("[spring-cloud-name-resolver] for {} shutdown", targetUri);
  }
}
