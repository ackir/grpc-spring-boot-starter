package demo;


import org.grpc.spring.boot.autoconfigure.annotation.GRPCLocalPort;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimpleApplication.class)
public class SimpleAppGrpcLocalPortInjectionTest {
  @Autowired     Environment environment;
  @GRPCLocalPort Integer     port;

  @Test
  public void name() {
    assertThat(port, Matchers.notNullValue());
    String property = environment.getProperty(GRPCLocalPort.GRPC_ADVERTIZED_LOCAL_PORT_PROPERTY_NAME);
    assertThat(property, Matchers.notNullValue());
  }

}
