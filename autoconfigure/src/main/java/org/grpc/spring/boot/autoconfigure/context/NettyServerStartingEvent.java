package org.grpc.spring.boot.autoconfigure.context;

import io.grpc.Server;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

/**
 * @author tolkv
 * @version 05/03/2017
 */
@Getter
public class NettyServerStartingEvent extends ApplicationEvent {
  private final ApplicationContext applicationContext;
  private final Server server;

  /**
   * Create a new ApplicationEvent.
   *
   * @param applicationContext
   * @param server the object on which the event initially occurred (never {@code null})
   */
  public NettyServerStartingEvent(ApplicationContext applicationContext, Server server) {
    super(applicationContext);
    this.applicationContext = applicationContext;
    this.server = server;
  }
}
