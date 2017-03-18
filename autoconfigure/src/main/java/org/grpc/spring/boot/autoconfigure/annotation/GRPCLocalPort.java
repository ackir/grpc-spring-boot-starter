package org.grpc.spring.boot.autoconfigure.annotation;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.*;

/**
 * @author tolkv
 * @version 05/03/2017
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
    ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Value("${local.grpc.server.port}")
public @interface GRPCLocalPort {
  String GRPC_ADVERTIZED_LOCAL_PORT_PROPERTY_NAME = "local.grpc.server.port";
}
