package demo;

import demo.grpc.health.HealthGrpc;
import demo.grpc.health.HealthOuterClass;
import io.grpc.stub.StreamObserver;
import org.grpc.spring.boot.autoconfigure.annotation.GRpcService;

import static demo.grpc.health.HealthOuterClass.*;

/**
 * @author tolkv
 * @since 13/03/16
 */
@GRpcService
public class HealthService extends HealthGrpc.HealthImplBase {
  @Override
  public void health(Empty request, StreamObserver<HealthOuterClass.HealthReply> responseObserver) {
    responseObserver.onNext(HealthReply.newBuilder().
        setStauts("OK")
        .build());
    responseObserver.onCompleted();
  }
}
