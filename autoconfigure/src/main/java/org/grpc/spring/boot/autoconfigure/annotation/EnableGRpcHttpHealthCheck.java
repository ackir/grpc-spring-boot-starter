package org.grpc.spring.boot.autoconfigure.annotation;

import org.grpc.spring.boot.autoconfigure.NettyHttpHealthCheckAutoConfiguration;
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
@Import(NettyHttpHealthCheckAutoConfiguration.class)
public @interface EnableGRpcHttpHealthCheck {

}
