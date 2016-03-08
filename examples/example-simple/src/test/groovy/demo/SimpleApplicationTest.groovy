package demo

import demo.grpc.calculator.CalculatorGrpc
import demo.grpc.calculator.CalculatorOuterClass
import io.grpc.ManagedChannel
import io.grpc.netty.NettyChannelBuilder
import io.grpc.stub.StreamObserver
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions

import static demo.grpc.calculator.CalculatorOuterClass.CalculateRequest.newBuilder

/**
 * @author tolkv
 * @since 08/03/16
 */
@SpringApplicationConfiguration(classes = SimpleApplication)
@IntegrationTest
class SimpleApplicationTest extends Specification {
  ManagedChannel channel

  def setup() {
    channel = NettyChannelBuilder.forAddress("localhost", 6565)
        .usePlaintext(true)
        .build();
  }

  def clean() {
    channel.shutdown()
  }

  def 'should calc 1+1 and + count of calls'() {
    given:
    def stub = CalculatorGrpc.newFutureStub(channel);
    def calcRequest = newBuilder()
        .setFirst(1)
        .setSecond(1)
        .build();

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
        println "summ: ${value.result}"
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
    observer.onNext(calcRequest)
    observer.onNext(calcRequest)
    observer.onNext(calcRequest)

    observer.onCompleted()

    then:
    conditions.await(2.0d)
  }
}
