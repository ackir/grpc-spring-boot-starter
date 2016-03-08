package org.grpc.spring.boot.autoconfigure.annotation;

import org.grpc.spring.boot.autoconfigure.GRpcAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author tolkv
 * @since 07/03/16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(GRpcAutoConfiguration.class)
public @interface EnableGRpcServer {

  /**
   * Base packages to scan for interfaces with @GRpcService annotation.
   */
  String[] basePackages() default {};
}
