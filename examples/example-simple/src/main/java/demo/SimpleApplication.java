package demo;

import org.grpc.spring.boot.autoconfigure.annotation.EnableGRpcHttpHealthCheck;
import org.grpc.spring.boot.autoconfigure.annotation.EnableGRpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author tolkv
 * @since 08/03/16
 */
@SpringBootApplication
@EnableGRpcServer
@EnableGRpcHttpHealthCheck
public class SimpleApplication {
  public static void main(String[] args) {
    SpringApplication.run(SimpleApplication.class, args);
  }
}
