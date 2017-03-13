package demo;

import demo.grpc.calculator.CalculatorGrpc;
import demo.grpc.calculator.CalculatorOuterClass;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.grpc.spring.boot.autoconfigure.annotation.GRpcService;

import java.util.concurrent.atomic.AtomicInteger;

import static demo.grpc.calculator.CalculatorOuterClass.CalculateRequest;
import static demo.grpc.calculator.CalculatorOuterClass.ResultReply;

/**
 * @author tolkv
 * @since 08/03/16
 */
@Slf4j
@GRpcService
public class CalculatorService extends CalculatorGrpc.CalculatorImplBase {
  private final AtomicInteger count = new AtomicInteger(0);

  @Override
  public void plus(CalculateRequest request, StreamObserver<CalculatorOuterClass.ResultReply> responseObserver) {
    int i = count.incrementAndGet();

    ResultReply result = ResultReply.newBuilder()
        .setResult(request.getFirst() + request.getSecond() + i)
        .build();

    responseObserver.onNext(result);
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<CalculateRequest> plusStream(StreamObserver<ResultReply> responseObserver) {
    return new StreamObserver<CalculateRequest>() {
      long currentSum = 0;

      @Override
      public void onNext(CalculateRequest value) {
        long first = value.getFirst();
        long second = value.getSecond();
        currentSum += first + second;

        log.info("Current sum: {}", currentSum);
        responseObserver.onNext(ResultReply.newBuilder()
            .setResult(currentSum)
            .build());
      }

      @Override
      public void onError(Throwable t) {
        log.error("Error during stream plus method in calc service", t);
      }

      @Override
      public void onCompleted() {
        log.info("Completed");
      }
    };
  }
}