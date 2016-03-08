package org.grpc.spring.boot.autoconfigure.annotation;

import org.springframework.stereotype.Service;
import java.lang.annotation.*;

/**
 * @author tolkv
 * @since 07/03/16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Service
public @interface GRpcService {
}
