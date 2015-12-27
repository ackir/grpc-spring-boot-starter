package org.grpc.spring.boot.autoconfigure.annotation;

import java.lang.annotation.*;

/**
 * @author tolkv
 * @since 07/03/16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GrpcService {
  /**
   * The path for mapping service to some URL
   * Default value is grpc service bean name
   */
  String path();
}
