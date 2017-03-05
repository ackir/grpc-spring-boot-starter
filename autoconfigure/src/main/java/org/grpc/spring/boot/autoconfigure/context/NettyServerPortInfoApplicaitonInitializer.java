package org.grpc.spring.boot.autoconfigure.context;

import lombok.extern.slf4j.Slf4j;
import org.grpc.spring.boot.autoconfigure.annotation.GRPCLocalPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tolkv
 * @version 05/03/2017
 */
@Slf4j
public class NettyServerPortInfoApplicaitonInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    applicationContext.addApplicationListener((ApplicationListener<NettyServerStartingEvent>)
        NettyServerPortInfoApplicaitonInitializer.this::onApplicationEvent);
  }

  private void onApplicationEvent(NettyServerStartingEvent event) {
    setPortProperty(event.getApplicationContext(),
        GRPCLocalPort.GRPC_ADVERTIZED_LOCAL_PORT_NAME,
        event.getServer().getPort());
  }

  private void setPortProperty(ApplicationContext context, String propertyName, int port) {
    if (context instanceof ConfigurableApplicationContext) {
      setPortProperty(((ConfigurableApplicationContext) context).getEnvironment(),
          propertyName, port);
    }
    if (context.getParent() != null) {
      setPortProperty(context.getParent(), propertyName, port);
    }
  }

  @SuppressWarnings("unchecked")
  private void setPortProperty(ConfigurableEnvironment environment, String propertyName, int port) {
    MutablePropertySources sources = environment.getPropertySources();
    PropertySource<?> source = sources.get(GRPCLocalPort.GRPC_ADVERTIZED_LOCAL_PORT_NAME);
    if (source == null) {
      source = new MapPropertySource(GRPCLocalPort.GRPC_ADVERTIZED_LOCAL_PORT_NAME, new HashMap<>());
      sources.addFirst(source);
    }
    ((Map<String, Object>) source.getSource()).put(propertyName, port);
  }
}
