package org.grpc.spring.boot.autoconfigure.discovery;

import io.grpc.Attributes;
import io.grpc.NameResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.URI;

@RequiredArgsConstructor
public class GrpcSpringCloudNameResolverFactory extends NameResolver.Factory {
  private final DiscoveryClient discoveryClient;

  @Override
  public NameResolver newNameResolver(URI targetUri, Attributes params) {
    return new GrpcSpringCloudNameResolver(targetUri, params, discoveryClient);
  }

  @Override
  public String getDefaultScheme() {
    return "spring";
  }
}
