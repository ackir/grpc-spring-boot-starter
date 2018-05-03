package demo;

import demo.grpc.calculator.CalculatorGrpc;
import demo.grpc.calculator.CalculatorOuterClass;
import demo.grpc.calculator.CalculatorOuterClass.CalculateRequest;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.util.RoundRobinLoadBalancerFactory;
import org.grpc.spring.boot.autoconfigure.annotation.GRPCLocalPort;
import org.grpc.spring.boot.autoconfigure.discovery.GrpcSpringCloudNameResolverFactory;
import org.grpc.spring.boot.autoconfigure.util.GrpcManagedChannelAutoClosableAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static demo.grpc.calculator.CalculatorOuterClass.CalculateRequest.newBuilder;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimpleApplication.class)
public class SimpleAppGrpcInProcessTest extends SimpleAppGrpcTransportAgnosticTest {


  @Override
  GrpcManagedChannelAutoClosableAdapter constructChannelForTarget(String target) {
    return new GrpcManagedChannelAutoClosableAdapter(InProcessChannelBuilder.forName(target)
        .usePlaintext(true)
        .build());
  }

}
