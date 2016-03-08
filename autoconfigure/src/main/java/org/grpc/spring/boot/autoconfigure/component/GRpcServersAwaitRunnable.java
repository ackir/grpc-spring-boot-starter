package org.grpc.spring.boot.autoconfigure.component;

import io.grpc.Server;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author tolkv
 * @since 08/03/16
 */
@Slf4j
public class GRpcServersAwaitRunnable implements Runnable {
  private final List<Server> servers;

  public GRpcServersAwaitRunnable(List<Server> servers) {
    this.servers = servers;
  }

  @Override
  public void run() {
    servers.forEach(server -> {
      try {
        server.awaitTermination();
      } catch (InterruptedException e) {
        log.error("GRpc server stopped with error", e);
      }
    });
  }
}
