package demo

import demo.grpc.calculator.CalculatorGrpc
import demo.grpc.calculator.CalculatorOuterClass
import demo.grpc.health.HealthGrpc
import io.grpc.ManagedChannel
import io.grpc.netty.NettyChannelBuilder
import io.grpc.stub.StreamObserver
import org.grpc.spring.boot.autoconfigure.annotation.GRPCLocalPort
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions

import static demo.grpc.calculator.CalculatorOuterClass.CalculateRequest.newBuilder
import static demo.grpc.health.HealthOuterClass.Empty
/**
 * @author tolkv
 * @since 08/03/16
 */
@SpringBootTest(classes = SimpleApplication)
class SimpleApplicationTest extends Specification {
  ManagedChannel channel

  @GRPCLocalPort
  Integer port

  def setup() {
    channel = NettyChannelBuilder.forAddress("localhost", port)
        .usePlaintext(true)
        .build()
  }

  def cleanup() {
    channel.shutdown()
  }

  def 'should calc 1+1 and + count of calls'() {
    given:
    def stub = CalculatorGrpc.newFutureStub(channel)
    def calcRequest = newBuilder()
        .setFirst(1)
        .setSecond(1)
        .build()

    expect:
    stub.plus(calcRequest).get().getResult() == replay

    where:
    replay | _
    3      | _
    4      | _
    5      | _
  }

  def 'should streaming'() {
    given:
    def conditions = new AsyncConditions()

    def stub = CalculatorGrpc.newStub(channel)
    def calcRequest = newBuilder()
        .setFirst(1)
        .setSecond(1)
        .build();

    def observer = stub.plusStream(new StreamObserver<CalculatorOuterClass.ResultReply>() {
      @Override
      void onNext(CalculatorOuterClass.ResultReply value) {
        conditions.evaluate {
          if (value.result == 6) //increase amount of sum for 2 number after each call
            assert value.result == 6
        }
      }

      @Override
      void onError(Throwable t) {
        t.printStackTrace()
      }

      @Override
      void onCompleted() {
        println 'Completed'
      }
    })

    when:
    3.times { observer.onNext calcRequest }

    observer.onCompleted()

    then:
    conditions.await(2.0d)
  }

  def 'health service should works'(){
    given:
    def stub = HealthGrpc.newFutureStub(channel)

    when:
    def health = stub.health(Empty.defaultInstance).get()

    then:
    health.stauts == 'OK'
  }
}
