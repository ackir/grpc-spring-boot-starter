package org.grpc.spring.boot.autoconfigure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tolkv
 * @since 07/03/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("grpc")
public class GRpcServerProperties {
  List<GRpcServerInstance> servers = new ArrayList<>(1);

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class GRpcServerInstance {
    Integer port;
    InetAddress address;
  }
}
