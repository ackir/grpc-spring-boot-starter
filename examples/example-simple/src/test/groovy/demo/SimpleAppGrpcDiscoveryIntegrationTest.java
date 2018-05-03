package demo;

import io.grpc.netty.NettyChannelBuilder;
import io.grpc.util.RoundRobinLoadBalancerFactory;
import org.grpc.spring.boot.autoconfigure.discovery.GrpcSpringCloudNameResolverFactory;
import org.grpc.spring.boot.autoconfigure.util.GrpcManagedChannelAutoClosableAdapter;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimpleApplication.class)
public class SimpleAppGrpcDiscoveryIntegrationTest extends SimpleAppGrpcTransportAgnosticTest {

  @Override
  GrpcManagedChannelAutoClosableAdapter constructChannelForTarget(String target) {
    return new GrpcManagedChannelAutoClosableAdapter(NettyChannelBuilder.forTarget(target)
        .usePlaintext(true)
        .loadBalancerFactory(RoundRobinLoadBalancerFactory.getInstance())
        .nameResolverFactory(new GrpcSpringCloudNameResolverFactory(discoveryClient))
        .build());
  }

}
