package org.grpc.spring.boot.autoconfigure.annotation;

import java.lang.annotation.*;

/**
 * @author tolkv
 * @since 07/03/16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableGrpcServer {

  /**
   * Base packages to scan for interfaces with @GrpcService annotation.
   */
  String[] basePackages() default {};
}
