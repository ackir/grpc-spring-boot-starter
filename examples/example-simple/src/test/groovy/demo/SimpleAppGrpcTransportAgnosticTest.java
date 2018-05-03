package demo;

import demo.grpc.calculator.CalculatorGrpc;
import demo.grpc.calculator.CalculatorOuterClass;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.util.RoundRobinLoadBalancerFactory;
import org.grpc.spring.boot.autoconfigure.annotation.GRPCLocalPort;
import org.grpc.spring.boot.autoconfigure.discovery.GrpcSpringCloudNameResolverFactory;
import org.grpc.spring.boot.autoconfigure.util.GrpcManagedChannelAutoClosableAdapter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static demo.grpc.calculator.CalculatorOuterClass.CalculateRequest.newBuilder;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public abstract class SimpleAppGrpcTransportAgnosticTest {
  @GRPCLocalPort int             grpcServerPort;
  @Autowired     DiscoveryClient discoveryClient;

  ThreadLocalRandom random = ThreadLocalRandom.current();

  @Test
  public void should_works_as_a_spec_default_java_grpc_client() throws Exception {

    try (GrpcManagedChannelAutoClosableAdapter channel = constructChannelForTarget("spring.calc-app")) {
      final CalculatorGrpc.CalculatorFutureStub serviceStub    = CalculatorGrpc.newFutureStub(channel);
      long                                      previousResult = 0;

      for (int i = 0; i < 100; i++) {
        CalculatorOuterClass.CalculateRequest calcRequest    = generateRandomCalcRequest();
        CalculatorOuterClass.ResultReply      resultReply    = serviceStub.plus(calcRequest).get(1, TimeUnit.SECONDS);
        long                                  currentResult  = resultReply.getResult();
        long                                  currentCounter = resultReply.getCount();


        assertThat("should eq to first+second+callCount == " + calcRequest.getFirst() + calcRequest.getSecond() + i + 1,
            currentResult - currentCounter,
            equalTo(calcRequest.getFirst() + calcRequest.getSecond())
        );

        previousResult = currentResult;
      }

    } catch (Exception e) {
      e.printStackTrace();
      fail("should not thrown");
    }


  }

  private CalculatorOuterClass.CalculateRequest generateRandomCalcRequest() {
    return newBuilder()
        .setFirst(1)
        .setSecond(1)
        .build();
  }

  abstract GrpcManagedChannelAutoClosableAdapter constructChannelForTarget(String target);

}
